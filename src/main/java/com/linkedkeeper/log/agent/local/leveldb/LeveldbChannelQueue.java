package com.linkedkeeper.log.agent.local.leveldb;

import com.linkedkeeper.log.agent.queue.AsyncQueue;
import com.linkedkeeper.log.agent.sender.Sender;

public class LeveldbChannelQueue extends AsyncQueue {

    /**
     * 抽象异步队列基类：‘发送消息’线程
     *
     * @param queueName
     */
    public LeveldbChannelQueue(Sender sender, String queueName) {
        super(sender, queueName);
    }

    public byte[] get() {
        return new byte[0];
    }

    public void add(String message) {

    }

    public void start() {

    }

    public void stop() {

    }
}
