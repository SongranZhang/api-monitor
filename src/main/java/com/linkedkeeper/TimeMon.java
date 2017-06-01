package com.linkedkeeper;

public class TimeMon extends DecoMon {

    protected long startTime;

    public TimeMon(MonKey key, MonInternals monData) {
        super(key, monData);
    }

    @Override
    public Monitor start() {
        super.start();
        startTime = System.currentTimeMillis();
        return this;
    }

    @Override
    public Monitor stop() {
        long endTime = System.currentTimeMillis();
        setAccessStats(endTime);
        add(endTime - startTime);
        super.stop();
        return this;
    }
}
