package com.linkedkeeper.log.agent.local.filechannel;

public class MessageWrapper {

    private final byte[] content;
    private final boolean firstMessage;
    private final int endPos;
    private final String currentFile;

    public MessageWrapper(byte[] content, boolean firstMessage, int endPos, String currentFile) {
        this.content = content;
        this.firstMessage = firstMessage;
        this.endPos = endPos;
        this.currentFile = currentFile;
    }

    public byte[] getContent() {
        return content;
    }

    public boolean isFirstMessage() {
        return firstMessage;
    }

    public int getEndPos() {
        return endPos;
    }

    public String getCurrentFile() {
        return currentFile;
    }
}
