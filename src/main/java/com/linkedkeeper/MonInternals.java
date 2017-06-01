package com.linkedkeeper;

import java.io.Serializable;

final class MonInternals implements Serializable {

    private static final long serialVersionUID = 1987L;

    /**
     * seed value to ensure that the first value always sets the max
     */
    static final double MAX_DOUBLE = -Double.MAX_VALUE;
    /**
     * seed value to ensure that the first value always sets the min
     */
    static final double MIN_DOUBLE = Double.MAX_VALUE;

    MonKey key;

    /**
     * the total for all values
     */
    double total = 0.0;
    /**
     * The minimum of all values
     */
    double min = MIN_DOUBLE;
    /**
     * The maximum of all values
     */
    double max = MAX_DOUBLE;
    /**
     * The total number of occurrences/calls to this object
     */
    double hits = 0.0;

    double errors = 0.0;
    /**
     * Intermediate value used to calculate std dev
     */
    double sumOfSquares = 0.0;
    /**
     * The most recent value that was passed to this object
     */
    double lastValue = 0.0;
    /**
     * The first time this object was accessed
     */
    long firstAccess = 0;
    /**
     * The last time this object was accessed
     */
    long lastAccess = 0;
    /**
     * Is this a time monitor object? Used for performance optimizations
     */
    boolean isTimeMonitor = false;

    double maxActive = 0.0;
    double totalActive = 0.0;
    boolean startHasBeenCalled = false;
    private ActivityStats activityStats;

    double thisActiveTotal;

    public void setActivityStats(ActivityStats stats) {
        this.activityStats = stats;
    }

    public double incrementThisActive() {
        return activityStats.thisActive.incrementAndReturn();
    }

    public void decrementThisActive() {
        activityStats.thisActive.decrement();
    }

    public void stop(double active) {
        totalActive += active;
        decrementThisActive();
    }

    public double getThisActiveCount() {
        return activityStats.thisActive.getCount();
    }

    public void reset() {
        hits = errors = total = sumOfSquares = lastValue = 0.0;
        firstAccess = lastAccess = 0;
        min = MIN_DOUBLE;
        max = MAX_DOUBLE;
        startHasBeenCalled = false;
        maxActive = totalActive = 0.0;
        activityStats.thisActive.setCount(0);
        thisActiveTotal = 0;
    }
}
