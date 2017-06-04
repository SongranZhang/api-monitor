package com.linkedkeeper.log.agent.sender.api;

import com.linkedkeeper.log.agent.sender.Sender;

public class SenderFactory extends SenderProperties {

    private static class SenderFactoryHolder {
        private static final SenderFactory INSTANCE = new SenderFactory();
    }

    public static final SenderFactory getInstance() {
        return SenderFactoryHolder.INSTANCE;
    }

    /**
     * Factory Pattern
     * Load local properties file to memory
     */
    private SenderFactory() {
        loadProperties();
    }

    public void offer(Sender sender, String key, String message) throws Exception {
        AsyncSender.getInstance().send(sender, key, message);
    }
}
