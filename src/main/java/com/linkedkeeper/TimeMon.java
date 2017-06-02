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
        getLog().setTime(startTime);
        return this;
    }

    @Override
    public Monitor stop() {
        long endTime = System.currentTimeMillis();
        setAccessStats(endTime);
        long elapsedTime = endTime - startTime;
        add(elapsedTime);
        super.stop();
        return this;
    }
}
