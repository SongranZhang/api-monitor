package com.linkedkeeper.log.agent.utils;

public class SleepUtil {

    public static void sleep(long mSec) {
        try {
            Thread.sleep(mSec);
        } catch (Exception ignore) {
            // Nothing to do
        }
    }

    public static void delay(int base, int factor) {
        sleep(base * (factor + 1));
    }

}
