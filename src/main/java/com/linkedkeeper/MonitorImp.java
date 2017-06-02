package com.linkedkeeper;

class MonitorImp extends Monitor {

    MonitorImp(MonInternals monData) {
        super(monData);
    }

    private static final MonKey NULL_MON_KEY = new NullMonKey();

    MonitorImp() {
        this(NULL_MON_KEY, null);
    }

    MonitorImp(MonKey key, ActivityStats activityStats) {
        this.monData.key = key;
        this.monData.setActivityStats(activityStats);
    }

    void setActivityStats(ActivityStats activityStats) {
        monData.setActivityStats(activityStats);
    }

    private static class NullMonKey implements MonKey {
        @Override
        public Object getValue(String primaryKey) {
            return "";
        }
    }
}
