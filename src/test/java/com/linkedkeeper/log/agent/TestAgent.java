package com.linkedkeeper.log.agent;

import com.linkedkeeper.log.agent.sender.Sender;
import com.linkedkeeper.log.agent.sender.api.SenderFactory;
import com.linkedkeeper.log.agent.utils.SleepUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-agent.xml")
public class TestAgent {

    private final static SenderFactory sf = SenderFactory.getInstance();

    private final static String key = "dataCollector";
    private final static String message = "Test Message No.";

    @Autowired
    private Sender sender;

    @Test
    public void test() {
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
