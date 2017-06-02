package com.linkedkeeper;

import java.util.concurrent.ConcurrentMap;

public class TestMonitor {

    public static void main(String[] args) throws Exception {
//        MonitorFactory.reset();
        for (int i = 0; i < 10; i++) {
            final String label = "sql" + i;
            new Mon(label).run();
        }
        ConcurrentMap<MonKey, Monitor> monitors = MonitorFactory.getMonitors();
        for (MonKey key : monitors.keySet()) {
            Monitor mon = monitors.get(key);
            System.out.println("label:" + mon.getLabel()
                    + ", units:" + mon.getUnits()
                    + ", hits:" + mon.getHits()
                    + ", errors:" + mon.getErrors()
                    + ", total:" + mon.getTotal()
                    + ", avg:" + mon.getAvg()
                    + ", min:" + mon.getMin()
                    + ", max:" + mon.getMax());
        }
    }
}

class Mon {

    private String label;

    public Mon(String label) {
        this.label = label;
    }

    public void run() {
        for (int i = 1; i <= 10; i++) {
            Monitor mon = null;
            try {
                mon = MonitorFactory.start(label);
                Thread.sleep(5);
                if (i % 3 == 0) {
                    throw new Exception("error.");
                }
                Thread.sleep(5);
            } catch (Exception e) {
                mon.error();
            } finally {
                mon.stop();
            }
            System.out.println(mon.getLog().toString());
        }
    }
}
