package com.linkedkeeper.log.agent.utils;

import java.util.Date;

public class PrintUtil {

    private final static String PROFILER = "dataCollector";

    private final static String INFO = PROFILER + " INFO ";

    private final static String WARN = PROFILER + " WARN ";

    private final static String DEBUG = PROFILER + " DEBUG ";

    private final static String ERROR = PROFILER + " ERROR ";

    public static void info(String message) {
        print(INFO + message);
    }

    public static void debug(String message) {
        print(DEBUG + message);
    }

    public static void warn(String message) {
        print(WARN + message);
    }

    public static void error(String message) {
        print(ERROR + message);
    }

    public static void print(String line) {
        System.out.println(new Date() + line);
    }

}
