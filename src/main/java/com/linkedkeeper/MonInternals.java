package com.linkedkeeper;

import java.io.Serializable;

final class MonInternals implements Serializable {

    private static final long serialVersionUID = 1987L;

    /**
     * seed value to ensure that the first value always sets the max
     */
    static final long MAX_DOUBLE = -Long.MAX_VALUE;
    /**
     * seed value to ensure that the first value always sets the min
     */
    static final long MIN_DOUBLE = Long.MAX_VALUE;

    MonKey key;

    /**
     * the total for all values
     */
    long total = 0;
    /**
     * The minimum of all values
     */
    long min = MIN_DOUBLE;
    /**
     * The maximum of all values
     */
    long max = MAX_DOUBLE;
    /**
     * The total number of occurrences/calls to this object
     */
    long hits = 0;

    long errors = 0;
    /**
     * Intermediate value used to calculate std dev
     */
    long sumOfSquares = 0;
    /**
     * The most recent value that was passed to this object
     */
    long lastValue = 0;
    /**
     * The first time this object was accessed
     */
    long firstAccess = 0;
    /**
     * The last time this object was accessed
     */
    long lastAccess = 0;

    long maxActive = 0;
    long totalActive = 0;
    boolean startHasBeenCalled = false;
    private ActivityStats activityStats;

    long thisActiveTotal;

    public void setActivityStats(ActivityStats stats) {
        this.activityStats = stats;
    }

    public long incrementThisActive() {
        return activityStats.thisActive.incrementAndReturn();
    }

    public void decrementThisActive() {
        activityStats.thisActive.decrement();
    }

    public void stop(long active) {
        totalActive += active;
        decrementThisActive();
    }

    public long getThisActiveCount() {
        return activityStats.thisActive.getCount();
    }

    public void reset() {
        hits = errors = total = sumOfSquares = lastValue = 0;
        firstAccess = lastAccess = 0;
        min = MIN_DOUBLE;
        max = MAX_DOUBLE;
        startHasBeenCalled = false;
        maxActive = totalActive = 0;
        activityStats.thisActive.setCount(0);
        thisActiveTotal = 0;
    }
}
