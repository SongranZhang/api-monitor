package com.linkedkeeper.log.agent.config;

public class QueueConstant {

    public final static String SENDER_PATTERN_MEMORY = "memory";

    public final static String DATA_FILE_SUFFIX = ".data";
    public final static String META_FILE_SUFFIX = ".meta";

    public final static int MAX_META_LEN = 2048;

    public final static long ASYNC_FLUSH_SECS = 2 * 1000;

    public final static int NO_DATA_WAIT_TIMEOUT = 2 * 1000;
    public final static int OPEN_CHANNEL_RETRY_COUNT = 3;
    public final static int OPEN_CHANNEL_RETRY_TIMEOUT = 5;
    public final static int MESSAGE_HEADER_LENGTH_LIMIT = 5 * 1024 * 1024;

    public static final String UTF_8 = "UTF-8";

}
