package com.mathapp.auth.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {
    @Mock
    private ChatProviderClient provider;

    private ChatAiProperties properties;
    private ChatSessionService service;

    @BeforeEach
    void setUp() {
        properties = new ChatAiProperties();
        properties.setEnabled(true);
        service = new ChatSessionService(provider, properties);
    }

    @Test
    void keepsDepartmentContextsSeparateAndTracksRemainingMessages() {
        when(provider.complete(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn("A math answer", "A physics answer");
        var mathSession = service.start("alice", ChatDepartment.MATHEMATICS);
        var physicsSession = service.start("alice", ChatDepartment.PHYSICS);

        var mathResponse = service.send("alice", mathSession.sessionId(), "What is a prime?");
        var physicsResponse = service.send("alice", physicsSession.sessionId(), "Explain gravity.");

        assertEquals("A math answer", mathResponse.response());
        assertEquals(9, mathResponse.remainingUserMessages());
        assertEquals(9, mathResponse.remainingAiResponses());
        assertEquals("A physics answer", physicsResponse.response());

        verify(provider).complete(ChatDepartment.MATHEMATICS,
                List.of(new ChatTurn("user", "What is a prime?")));
        verify(provider).complete(ChatDepartment.PHYSICS,
                List.of(new ChatTurn("user", "Explain gravity.")));
    }

    @Test
    void capsUserMessagesAtTenAndTruncatesResponsesToOneThousandWords() {
        String longAnswer = "word ".repeat(1001);
        when(provider.complete(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(longAnswer);
        var session = service.start("alice", ChatDepartment.DSA);

        var first = service.send("alice", session.sessionId(), "Explain binary search.");
        for (int index = 1; index < ChatSessionService.MAX_USER_MESSAGES; index++) {
            service.send("alice", session.sessionId(), "More detail.");
        }

        assertEquals(ChatSessionService.MAX_RESPONSE_WORDS,
                first.response().split("(?U)\\s+").length);
        ChatSessionException exception = assertThrows(ChatSessionException.class,
                () -> service.send("alice", session.sessionId(), "One more."));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.status());
    }

    @Test
    void doesNotExposeAnotherUsersSessionAndExitRemovesOwnedSession() {
        var session = service.start("alice", ChatDepartment.MATHEMATICS);

        ChatSessionException unauthorized = assertThrows(ChatSessionException.class,
                () -> service.send("bob", session.sessionId(), "A question"));
        assertEquals(HttpStatus.NOT_FOUND, unauthorized.status());

        service.exit("alice", session.sessionId());
        ChatSessionException expired = assertThrows(ChatSessionException.class,
                () -> service.send("alice", session.sessionId(), "A question"));
        assertEquals(HttpStatus.NOT_FOUND, expired.status());
    }

    @Test
    void providerFailureDoesNotConsumeSessionMessageLimit() {
        when(provider.complete(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList()))
                .thenThrow(new ChatProviderException())
                .thenReturn("Try again.");
        var session = service.start("alice", ChatDepartment.PHYSICS);

        assertThrows(ChatProviderException.class,
                () -> service.send("alice", session.sessionId(), "Explain velocity."));
        var retry = service.send("alice", session.sessionId(), "Explain velocity.");

        assertEquals(9, retry.remainingUserMessages());
    }

    @Test
    void suppliesOnlyTheSelectedSessionHistoryWithEachRequest() {
        when(provider.complete(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn("First answer", "Second answer");
        var session = service.start("alice", ChatDepartment.DSA);

        service.send("alice", session.sessionId(), "What is a stack?");
        service.send("alice", session.sessionId(), "How does push work?");

        verify(provider).complete(ChatDepartment.DSA, List.of(
                new ChatTurn("user", "What is a stack?")));
        verify(provider).complete(ChatDepartment.DSA, List.of(
                new ChatTurn("user", "What is a stack?"),
                new ChatTurn("assistant", "First answer"),
                new ChatTurn("user", "How does push work?")));
    }

    @Test
    void rejectsStartingAndSendingWhenAiIsDisabled() {
        properties.setEnabled(false);

        ChatSessionException startError = assertThrows(ChatSessionException.class,
                () -> service.start("alice", ChatDepartment.MATHEMATICS));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, startError.status());

        properties.setEnabled(true);
        var session = service.start("alice", ChatDepartment.MATHEMATICS);
        properties.setEnabled(false);
        ChatSessionException sendError = assertThrows(ChatSessionException.class,
                () -> service.send("alice", session.sessionId(), "A question"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, sendError.status());
    }
}
