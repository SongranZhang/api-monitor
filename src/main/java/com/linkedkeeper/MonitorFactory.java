package com.linkedkeeper;

import java.util.concurrent.ConcurrentMap;

public class MonitorFactory {

    private static MonitorFactoryInterface factory;

    static {
        // enable the factory by default.
        init();
    }

    public static void reset() {
        init();
    }

    private static void init() {
        factory = new FactoryEnabled();
    }

    /**
     * Return a timing monitor with units in milliseconds.  stop() should be called on the returned monitor to indicate the time
     * that the process took. Note time monitors keep the startTime as an instance variable and so every time you want to use a TimeMonitor
     * you should get a new instance.
     * <p>
     * <p>Sample call:</p>
     * <pre>{@code
     *  Monitor mon=MonitorFactory.start("pageHits");
     *   ...code being timed...
     *  mon.stop();
     * }</pre>
     */
    public static Monitor start(String label) {
        return factory.start(label);
    }

    public static ConcurrentMap<MonKey, Monitor> getMonitors() {
        return factory.getMap();
    }
}
