package com.linkedkeeper.api.monitor;

import java.io.Serializable;

public class MonLog implements Serializable {

    private long time;
    private String key;
    private int processState = 0; // 0 正常，1 异常
    private long elapsedTime;
    private long count;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getProcessState() {
        return processState;
    }

    public void setProcessState(int processState) {
        this.processState = processState;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "MonLog: {" +
                "time=" + time +
                ", key='" + key + '\'' +
                ", processState=" + processState +
                ", elapsedTime=" + elapsedTime +
                ", count=" + count +
                '}';
    }
}
