package com.linkedkeeper;

import java.io.Serializable;

/**
 * Simple counter class used to track activity stats
 */
final class Counter implements Serializable {

    private long count;

    /**
     * explicitly set the counters value
     */
    public void setCount(long value) {
        count = value;
    }

    /**
     * return the counters value
     */
    public long getCount() {
        return count;
    }

    /**
     * decrement the counters value
     */
    public void decrement() {
        --count;
    }

    /**
     * increment the counters value
     */
    public void increment() {
        ++count;
    }

    /**
     * increment the counters value and return it
     */
    public long incrementAndReturn() {
        return ++count;
    }
}
