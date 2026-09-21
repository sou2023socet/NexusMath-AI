package mathapp.stats.core;

import javafx.application.Platform;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Reactive Event Bus providing sub-16ms throttled event dispatching to thread-safe listeners.
 */
public class StatisticalDataBus {

    private static StatisticalDataBus instance;
    private final StatisticalState state;
    private final Map<StatisticalEventType, List<StatisticalEventListener>> listenersMap;
    
    // Throttling engine to prevent UI lag on rapid slider movements
    private final ScheduledExecutorService throttleExecutor;
    private final Map<StatisticalEventType, ScheduledFuture<?>> pendingTasks;
    private static final long THROTTLE_DELAY_MS = 16; // Target ~60 FPS update rate

    private StatisticalDataBus() {
        this.state = new StatisticalState();
        this.listenersMap = new ConcurrentHashMap<>();
        this.throttleExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "StatsDataBus-Throttler");
            t.setDaemon(true);
            return t;
        });
        this.pendingTasks = new EnumMap<>(StatisticalEventType.class);

        for (StatisticalEventType type : StatisticalEventType.values()) {
            listenersMap.put(type, new CopyOnWriteArrayList<>());
        }
    }

    public static synchronized StatisticalDataBus getInstance() {
        if (instance == null) {
            instance = new StatisticalDataBus();
        }
        return instance;
    }

    public StatisticalState getState() {
        return state;
    }

    /**
     * Subscribe a listener to a specific event type.
     */
    public void subscribe(StatisticalEventType type, StatisticalEventListener listener) {
        if (listener != null) {
            listenersMap.get(type).add(listener);
        }
    }

    /**
     * Unsubscribe a listener from a specific event type.
     */
    public void unsubscribe(StatisticalEventType type, StatisticalEventListener listener) {
        if (listener != null) {
            listenersMap.get(type).remove(listener);
        }
    }

    /**
     * Publish an event immediately on the JavaFX Application Thread.
     */
    public void publishImmediate(StatisticalEventType type) {
        Runnable dispatchRunnable = () -> {
            List<StatisticalEventListener> listeners = listenersMap.get(type);
            for (StatisticalEventListener listener : listeners) {
                try {
                    listener.onStatisticalStateChanged(type, state);
                } catch (Exception e) {
                    System.err.println("Error dispatching event " + type + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        if (Platform.isFxApplicationThread()) {
            dispatchRunnable.run();
        } else {
            Platform.runLater(dispatchRunnable);
        }
    }

    /**
     * Publish a throttled event (debounced to 16ms window) for high-frequency user interactions (sliders, drags).
     */
    public synchronized void publishThrottled(StatisticalEventType type) {
        ScheduledFuture<?> existingTask = pendingTasks.get(type);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }

        ScheduledFuture<?> newTask = throttleExecutor.schedule(() -> {
            publishImmediate(type);
        }, THROTTLE_DELAY_MS, TimeUnit.MILLISECONDS);

        pendingTasks.put(type, newTask);
    }
}