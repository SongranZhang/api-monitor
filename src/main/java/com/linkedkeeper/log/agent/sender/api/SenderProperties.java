package com.linkedkeeper.log.agent.sender.api;

import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.io.InputStream;
import java.util.Properties;

public class SenderProperties {

    private static final String PROFILER_CONFIG_FILE = "logagent.properties";

    protected void loadProperties() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROFILER_CONFIG_FILE);
        Properties prop = new Properties();

        if (in == null) {
            PrintUtil.warn("The profile.properties is not exist, maybe configure by code.");
            return;
        }

        try {
            prop.load(in);
            // Load config from the .properties to the memory
            SenderConfig.getInstance().setAll(prop);
            PrintUtil.info("Profiler configuration is successful.");
        } catch (Exception e) {
            PrintUtil.error("An error occurred when the configuration parameters." + e);
        } finally {
            try {
                in.close();
                prop.clear();
            } catch (Exception e) {
                // Nothing to do
            }
        }
    }
}
