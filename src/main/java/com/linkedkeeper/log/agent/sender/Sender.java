package com.linkedkeeper.log.agent.sender;

public interface Sender {

    void send(String id, String message) throws Exception;

}