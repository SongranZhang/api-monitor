package com.linkedkeeper;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FactoryEnabled implements MonitorFactoryInterface {

    /**
     * Creates a new instance of FactoryEnabled.
     */
    public FactoryEnabled() {
        initialize();
    }

    private ConcurrentMap<MonKey, Monitor> map;
    private GetMonitor getMonitor;
    private int maxMonitors;

    private static final boolean TIME_MONITOR = true;
    private static final int DEFAULT_MAP_SIZE = 500;
    private static final NullMonitor NULL_MON = new NullMonitor();

    private synchronized void initialize() {
        setMap(new ConcurrentHashMap<MonKey, Monitor>(DEFAULT_MAP_SIZE));
    }

    public void setMap(ConcurrentMap<MonKey, Monitor> map) {
        this.map = map;
        this.getMonitor = new GetMonitor();
    }

    public ConcurrentMap<MonKey, Monitor> getMap() {
        return map;
    }

    public int getMaxNumMonitors() {
        return maxMonitors;
    }

    public void setMaxNumMonitors(int maxMonitors) {
        this.maxMonitors = maxMonitors;
    }

    @Override
    public Monitor start(String label) {
        return getTimeMonitor(new MonKeyImp(label, "ms.")).start();
    }

    private MonitorImp getTimeMonitor(MonKey key) {
        return getMonitor(key, TIME_MONITOR);
    }

    private MonitorImp getMonitor(MonKey key, boolean isTimePrimary) {
        MonitorImp mon = getMonitor.getMon(key, isTimePrimary);
        return isTimePrimary ? new TimeMon(key, mon.getMonInternals()) : new DecoMon(key, mon.getMonInternals());
    }

    private MonitorImp createMon(MonKey key, boolean isTimeMonitor) {
        ActivityStats activityStats = new ActivityStats(new Counter());
        return new MonitorImp(key, activityStats, isTimeMonitor);
    }

    private MonitorImp getExistingMonitor(MonKey key) {
        return (MonitorImp) map.get(key);
    }

    private boolean monitorThresholdReached() {
        return (maxMonitors > 0 && map.size() >= maxMonitors);
    }

    private class GetMonitor implements Serializable {
        protected MonitorImp getMon(MonKey key, boolean isTimeMonitor) {
            MonitorImp mon = getExistingMonitor(key);
            if (mon == null) {
                if (monitorThresholdReached()) {
                    return NULL_MON;
                }
                mon = createMon(key, isTimeMonitor);
                MonitorImp tempMon = (MonitorImp) map.putIfAbsent(key, mon);
                if (tempMon != null) {
                    mon = tempMon;
                }
            }
            return mon;
        }
    }
}
