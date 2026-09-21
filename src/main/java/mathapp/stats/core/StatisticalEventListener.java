package mathapp.stats.core;

/**
 * Functional interface for subscribing to reactive statistical state updates.
 */
@FunctionalInterface
public interface StatisticalEventListener {
    /**
     * Triggered when a state mutation event is published through the StatisticalDataBus.
     *
     * @param type  The type of mutation event that occurred.
     * @param state The updated immutable snapshot or live state container.
     */
    void onStatisticalStateChanged(StatisticalEventType type, StatisticalState state);
}