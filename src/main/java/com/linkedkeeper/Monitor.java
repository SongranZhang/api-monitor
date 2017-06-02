package com.linkedkeeper;

import java.io.Serializable;

public abstract class Monitor implements MonitorInt, Serializable {

    // Internal data passed from monitor to monitor.
    protected MonInternals monData;
    private long active;

    protected Monitor(MonInternals monData) {
        this.monData = monData;
    }

    Monitor() {
        this(new MonInternals());
    }

    final MonInternals getMonInternals() {
        synchronized (monData) {
            return monData;
        }
    }

    public long getHits() {
        synchronized (monData) {
            return monData.hits;
        }
    }

    public long getErrors() {
        synchronized (monData) {
            return monData.errors;
        }
    }

    public long getTotal() {
        synchronized (monData) {
            return monData.total;
        }
    }

    public long getAvg() {
        return avg(monData.total);
    }

    public long getMin() {
        synchronized (monData) {
            return monData.min;
        }
    }

    public long getMax() {
        synchronized (monData) {
            return monData.max;
        }
    }

    public String getLabel() {
        return (String) getMonKey().getValue(MonKey.LABEL_HEADER);
    }

    public String getUnits() {
        return (String) getMonKey().getValue(MonKey.UNITS_HEADER);
    }

    public long getLastValue() {
        synchronized (monData) {
            return monData.lastValue;
        }
    }

    public long getLastAccess() {
        synchronized (monData) {
            return monData.lastAccess;
        }
    }

    public void setAccessStats(long now) {
        synchronized (monData) {
            if (monData.firstAccess == 0) {
                monData.firstAccess = now;
            }
            monData.lastAccess = now;
        }
    }

    public void reset() {
        synchronized (monData) {
            monData.reset();
        }
    }

    public MonKey getMonKey() {
        return monData.key;
    }

    public Monitor start() {
        synchronized (monData) {
            active = monData.incrementThisActive();
        }
        return this;
    }

    public Monitor stop() {
        synchronized (monData) {
            if (active >= monData.maxActive) {
                monData.maxActive = active;
            }
            monData.stop(active);
        }
        return this;
    }

    public Monitor add(long value) {
        synchronized (monData) {
            setAccessStats(System.currentTimeMillis());

            monData.lastValue = value;
            monData.hits++;
            monData.total += value;
            monData.sumOfSquares += value * value;

            if (value <= monData.min) {
                monData.min = value;
            }
            if (value >= monData.max) {
                monData.max = value;
            }
        }
        return this;
    }

    public Monitor error() {
        synchronized (monData) {
            monData.errors++;
        }
        return this;
    }

    public long getActive() {
        synchronized (monData) {
            return monData.getThisActiveCount();
        }
    }

    public long getMaxActive() {
        synchronized (monData) {
            return monData.maxActive;
        }
    }

    private long avg(long value) {
        synchronized (monData) {
            if (monData.hits == 0)
                return 0;
            else
                return value / monData.hits;
        }
    }

    public double getStdDev() {
        synchronized (monData) {
            double stdDeviation = 0;
            if (monData.hits != 0) {
                long sumOfX = monData.total;
                long n = monData.hits;
                long nMinus1 = (n <= 1) ? 1 : n - 1; // avoid 0 divides;

                long numerator = monData.sumOfSquares
                        - ((sumOfX * sumOfX) / n);
                stdDeviation = java.lang.Math.sqrt(numerator / nMinus1);
            }

            return stdDeviation;
        }
    }
}
