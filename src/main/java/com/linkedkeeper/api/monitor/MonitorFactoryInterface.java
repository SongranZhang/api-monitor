package com.linkedkeeper.api.monitor;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

public interface MonitorFactoryInterface extends Serializable {

    /**
     * Return a time monitor (the units are implied and are ms. Note activity stats are incremented
     */
    Monitor start(String label);

    ConcurrentMap<MonKey, Monitor> getMap();

}
