package com.linkedkeeper;

import java.io.Serializable;

public abstract class Monitor implements MonitorInt, Serializable {

    // Internal data passed from monitor to monitor.
    protected MonInternals monData;
    private double active;

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

    public double getHits() {
        synchronized (monData) {
            return monData.hits;
        }
    }

    public double getErrors() {
        synchronized (monData) {
            return monData.errors;
        }
    }

    public double getTotal() {
        synchronized (monData) {
            return monData.total;
        }
    }

    public double getAvg() {
        return avg(monData.total);
    }

    public double getMin() {
        synchronized (monData) {
            return monData.min;
        }
    }

    public double getMax() {
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

    public double getLastValue() {
        synchronized (monData) {
            return monData.lastValue;
        }
    }

    public double getLastAccess() {
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
            if (!monData.startHasBeenCalled) {
                monData.startHasBeenCalled = true;
            }
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

    public Monitor add(double value) {
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

    public double getActive() {
        synchronized (monData) {
            return monData.getThisActiveCount();
        }
    }

    public double getMaxActive() {
        synchronized (monData) {
            return monData.maxActive;
        }
    }

    private double avg(double value) {
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
                double sumOfX = monData.total;
                double n = monData.hits;
                double nMinus1 = (n <= 1) ? 1 : n - 1; // avoid 0 divides;

                double numerator = monData.sumOfSquares
                        - ((sumOfX * sumOfX) / n);
                stdDeviation = java.lang.Math.sqrt(numerator / nMinus1);
            }

            return stdDeviation;
        }
    }
}
