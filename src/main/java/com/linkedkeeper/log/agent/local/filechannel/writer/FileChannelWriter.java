package com.linkedkeeper.log.agent.local.filechannel.writer;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.local.filechannel.FileChannelQueue;
import com.linkedkeeper.log.agent.queue.AsyncTask;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class FileChannelWriter implements AsyncTask {

    protected final FileChannelQueue fileChannelQueue;
    // nio
    protected MappedByteBuffer writeMappedByteBuffer;
    protected FileChannel writeFileChannel;

    public FileChannelWriter(FileChannelQueue fileChannelQueue) {
        this.fileChannelQueue = fileChannelQueue;
    }

    public abstract void write(String message);

    public static FileChannelWriter createWriter(String name, FileChannelQueue fileChannelQueue) {
        if (QueueConstant.SENDER_PATTERN_MEMORY.equals(name)) {
            return new MemoryChannelWriter(fileChannelQueue);
        }
        return new MMapChannelWriter(fileChannelQueue);
    }

    protected void checkWriteChannel(int messageSize) {
        int rotationSize = SenderConfig.getInstance().getRotationSize();
        try {
            if (writeFileChannel == null) {
                writeFileChannel = new RandomAccessFile(fileChannelQueue.getDataFileManager().createRotationFile(), "rw").getChannel();
                writeMappedByteBuffer = writeFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, rotationSize);
            }
            if (writeMappedByteBuffer.position() + messageSize > rotationSize) {
                writeMappedByteBuffer.force();
                writeFileChannel.close();
                writeFileChannel = new RandomAccessFile(fileChannelQueue.getDataFileManager().createRotationFile(), "rw").getChannel();
                writeMappedByteBuffer = writeFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, rotationSize);
            }
        } catch (Exception e) {
            throw new RuntimeException("Create write channel failed", e);
        }
    }
}
