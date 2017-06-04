package com.linkedkeeper.log.agent.config;

import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.util.Properties;

public class SenderConfig {

    private SenderConfig() {
    }

    private static class SenderConfigHolder {
        private static final SenderConfig INSTANCE = new SenderConfig();
    }

    public static final SenderConfig getInstance() {
        return SenderConfigHolder.INSTANCE;
    }

    private boolean printExceptionStack;
    private String metaPath;
    private String dataPath;
    private int rotationSize;
    private String pattern;

    public void setAll(Properties prop) {
        // Optional
        String appHome = getClass().getResource("/").getPath() + "../";
        String metaPath = prop.getProperty("metaPath", "./dataCollector/profiler/meta");
        if (!metaPath.startsWith("/")) {
            setMetaPath(appHome + "/" + metaPath);
        } else {
            setMetaPath(metaPath);
        }
        String dataPath = prop.getProperty("dataPath", "./dataCollector/profiler/data");
        if (!dataPath.startsWith("/")) {
            setDataPath(appHome + "/" + dataPath);
        } else {
            setDataPath(dataPath);
        }

        setPrintExceptionStack(Boolean.parseBoolean(prop.getProperty("printExceptionStack", "true")));
        setRotationSize(Integer.parseInt(prop.getProperty("rotationSize", "104857600")));
        setPattern(prop.getProperty("pattern"));

        PrintUtil.debug("[init] Load SenderConfig :" + this.toString());
    }

    public boolean isPrintExceptionStack() {
        return printExceptionStack;
    }

    public void setPrintExceptionStack(boolean printExceptionStack) {
        this.printExceptionStack = printExceptionStack;
    }

    public String getMetaPath() {
        return metaPath;
    }

    public void setMetaPath(String metaPath) {
        this.metaPath = metaPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public int getRotationSize() {
        return rotationSize;
    }

    public void setRotationSize(int rotationSize) {
        this.rotationSize = rotationSize;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return "SenderConfig{" +
                "printExceptionStack=" + printExceptionStack +
                ", metaPath='" + metaPath + '\'' +
                ", dataPath='" + dataPath + '\'' +
                ", rotationSize=" + rotationSize +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
