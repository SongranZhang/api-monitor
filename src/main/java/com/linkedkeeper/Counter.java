package com.linkedkeeper;

import java.io.Serializable;

/**
 * Simple counter class used to track activity stats
 */
final class Counter implements Serializable {

    private double count;

    /**
     * explicitly set the counters value
     */
    public void setCount(double value) {
        count = value;
    }

    /**
     * return the counters value
     */
    public double getCount() {
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
    public double incrementAndReturn() {
        return ++count;
    }
}
