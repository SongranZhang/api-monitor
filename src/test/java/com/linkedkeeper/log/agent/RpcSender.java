package com.linkedkeeper.log.agent;

import com.linkedkeeper.log.agent.sender.Sender;

public class RpcSender implements Sender {

    public void send(String id, String message) throws Exception {
        System.out.println("id: " + id + ", message: " + message);
    }
}
