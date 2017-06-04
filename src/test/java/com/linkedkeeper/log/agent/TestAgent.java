package com.linkedkeeper.log.agent;

import com.linkedkeeper.log.agent.sender.Sender;
import com.linkedkeeper.log.agent.sender.api.SenderFactory;
import com.linkedkeeper.log.agent.utils.SleepUtil;

public class TestAgent {

    private final static SenderFactory sf = SenderFactory.getInstance();

    private final static String key = "dataCollector";
    private final static String message = "Test Message No.";

    private static Sender sender = new RpcSender();

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 500; i++) {
                sf.offer(sender, key, message + i);
                SleepUtil.sleep(300);
            }
        } catch (Exception e) {
            // Nothing to do
        }
    }
}
