package com.mathapp.auth.chat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ChatSessionService {
    public static final int MAX_USER_MESSAGES = 10;
    public static final int MAX_RESPONSE_WORDS = 1000;
    private static final int MAX_ACTIVE_SESSIONS_PER_USER = 3;
    private static final int MAX_ACTIVE_SESSIONS = 10_000;
    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);
    private static final Duration RATE_WINDOW = Duration.ofMinutes(1);

    private final Map<UUID, ChatSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, RateWindow> rateWindows = new ConcurrentHashMap<>();
    private final Object sessionCreationLock = new Object();
    private final ChatProviderClient provider;
    private final ChatAiProperties properties;

    public ChatSessionService(ChatProviderClient provider, ChatAiProperties properties) {
        this.provider = provider;
        this.properties = properties;
    }

    public SessionState start(String owner, ChatDepartment department) {
        ensureEnabled();
        acquireRequest(owner);
        removeExpiredSessions();
        synchronized (sessionCreationLock) {
            long ownedSessions = sessions.values().stream()
                    .filter(session -> session.owner.equals(owner))
                    .count();
            if (ownedSessions >= MAX_ACTIVE_SESSIONS_PER_USER || sessions.size() >= MAX_ACTIVE_SESSIONS) {
                throw new ChatSessionException(HttpStatus.TOO_MANY_REQUESTS,
                        "Too many active chat sessions. Exit an existing chat and try again.");
            }

            ChatSession session = new ChatSession(UUID.randomUUID(), owner, department);
            sessions.put(session.id, session);
            return session.toState();
        }
    }

    public MessageState send(String owner, UUID sessionId, String message) {
        ensureEnabled();
        ChatSession session = requireSession(owner, sessionId);
        synchronized (session) {
            ensureActive(session);
            acquireRequest(owner);
            if (session.userMessages >= MAX_USER_MESSAGES) {
                throw new ChatSessionException(HttpStatus.TOO_MANY_REQUESTS,
                        "This chat has reached its 10-message limit. Start a new chat to continue.");
            }

            List<ChatTurn> requestHistory = new ArrayList<>(session.history);
            requestHistory.add(new ChatTurn("user", message));
            String answer = limitWords(provider.complete(session.department, List.copyOf(requestHistory)));
            session.history.add(new ChatTurn("user", message));
            session.history.add(new ChatTurn("assistant", answer));
            session.userMessages++;
            session.lastActivity = Instant.now();
            return new MessageState(answer, MAX_USER_MESSAGES - session.userMessages,
                    MAX_USER_MESSAGES - session.userMessages);
        }
    }

    public void exit(String owner, UUID sessionId) {
        ChatSession session = requireSession(owner, sessionId);
        synchronized (session) {
            if (!sessions.remove(sessionId, session)) {
                throw sessionNotFound();
            }
            session.history.clear();
            session.userMessages = 0;
        }
    }

    private ChatSession requireSession(String owner, UUID sessionId) {
        ChatSession session = sessions.get(sessionId);
        if (session == null) {
            throw sessionNotFound();
        }
        synchronized (session) {
            ensureActive(session);
            if (!session.owner.equals(owner)) {
                throw sessionNotFound();
            }
            return session;
        }
    }

    private void ensureActive(ChatSession session) {
        if (sessions.get(session.id) != session || expired(session)) {
            sessions.remove(session.id, session);
            session.history.clear();
            throw sessionNotFound();
        }
    }

    private void removeExpiredSessions() {
        Instant cutoff = Instant.now().minus(SESSION_TTL);
        sessions.forEach((id, session) -> {
            synchronized (session) {
                if (session.lastActivity.isBefore(cutoff) && sessions.remove(id, session)) {
                    session.history.clear();
                    session.userMessages = 0;
                }
            }
        });
    }

    @Scheduled(fixedDelay = 60_000)
    public void clearExpiredSessions() {
        removeExpiredSessions();
    }

    private boolean expired(ChatSession session) {
        return session.lastActivity.plus(SESSION_TTL).isBefore(Instant.now());
    }

    private synchronized void acquireRequest(String owner) {
        Instant now = Instant.now();
        rateWindows.entrySet().removeIf(entry ->
                entry.getValue().startedAt.plus(RATE_WINDOW.multipliedBy(2)).isBefore(now));
        RateWindow window = rateWindows.get(owner);
        if (window == null || window.startedAt.plus(RATE_WINDOW).isBefore(now)) {
            rateWindows.put(owner, new RateWindow(now, 1));
            return;
        }
        if (window.count >= MAX_REQUESTS_PER_MINUTE) {
            throw new ChatSessionException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many chat requests. Please wait a minute and try again.");
        }
        window.count++;
    }

    private static String limitWords(String response) {
        String normalized = response == null ? "" : response.strip();
        if (normalized.isEmpty()) {
            throw new ChatProviderException();
        }
        String[] words = normalized.split("(?U)\\s+");
        if (words.length <= MAX_RESPONSE_WORDS) {
            return normalized;
        }
        return String.join(" ", java.util.Arrays.copyOf(words, MAX_RESPONSE_WORDS - 1)) + " …";
    }

    private static ChatSessionException sessionNotFound() {
        return new ChatSessionException(HttpStatus.NOT_FOUND,
                "Chat session was not found or has expired.");
    }

    private void ensureEnabled() {
        if (!properties.isEnabled()) {
            throw new ChatSessionException(HttpStatus.SERVICE_UNAVAILABLE,
                    "AI chat is disabled by backend configuration.");
        }
    }

    public record SessionState(UUID sessionId, String department, int remainingUserMessages,
                               int remainingAiResponses) {
    }

    public record MessageState(String response, int remainingUserMessages, int remainingAiResponses) {
    }

    private static final class ChatSession {
        private final UUID id;
        private final String owner;
        private final ChatDepartment department;
        private final List<ChatTurn> history = new ArrayList<>();
        private int userMessages;
        private Instant lastActivity = Instant.now();

        private ChatSession(UUID id, String owner, ChatDepartment department) {
            this.id = id;
            this.owner = owner;
            this.department = department;
        }

        private SessionState toState() {
            return new SessionState(id, department.name(), MAX_USER_MESSAGES, MAX_USER_MESSAGES);
        }
    }

    private static final class RateWindow {
        private final Instant startedAt;
        private int count;

        private RateWindow(Instant startedAt, int count) {
            this.startedAt = startedAt;
            this.count = count;
        }
    }
}
