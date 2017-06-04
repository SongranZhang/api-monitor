package com.linkedkeeper.log.agent.local.filechannel.writer;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.local.filechannel.FileChannelQueue;
import com.linkedkeeper.log.agent.utils.PrintUtil;
import com.linkedkeeper.log.agent.utils.SleepUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * MappedByteBuffer 将文件直接映射到内存（这里的内存指的是虚拟内存，并不是物理内存）
 * FileChannel提供了map方法来把文件影射为内存映像文件： MappedByteBuffer map(int mode,long position,long size);
 * 可以把文件的从position开始的size大小的区域映射为内存映像文件，mode指出了可访问该内存映像文件的方式：READ_ONLY,READ_WRITE,PRIVATE.
 * 主要方法：
 * (1) put() 向映射文件写入字节
 * (2) force() 强制写入到文件
 * (3) get(int len) 读取指定索引处的字节。
 */
public class MMapChannelWriter extends FileChannelWriter {

    private final ExecutorService writerTask;
    private boolean stopped = false;

    public MMapChannelWriter(FileChannelQueue fileChannelQueue) {
        super(fileChannelQueue);
        final String queueName = fileChannelQueue.getQueueName();
        this.writerTask = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profiler-MMapWriteTask-" + queueName);
            }
        });
    }

    public synchronized void write(String message) {
        try {
//            byte[] cb = message.getBytes(Charset.forName(QueueConstant.UTF_8));
            byte[] cb = message.getBytes();
            if (cb.length == 0) {
                return;
            }
            int messageSize = 4 + cb.length;
            checkWriteChannel(messageSize);
            // nio
            writeMappedByteBuffer.putInt(cb.length);
            writeMappedByteBuffer.put(cb);
        } catch (Exception e) {
            e.printStackTrace();
            PrintUtil.error("MMapChannelWriter write failure, message -> " + message + ", error -> " + e);
            throw new RuntimeException("Write message failed.", e);
        }

        fileChannelQueue.getLatch().countDown();
    }

    public void start() {
        writerTask.submit(new Runnable() {
            public void run() {
                try {
                    asyncFlush();
                } catch (Throwable t) {
                    PrintUtil.error("AsyncFlush() unHandle Exception. " + t);
                }
                PrintUtil.info("AsyncFlush() stopped.");
            }
        });
    }

    /**
     * fore() 缓冲区是READ_WRITE模式下，此方法对缓冲区内容的修改强行写入文件
     */
    private void asyncFlush() {
        while (!stopped) {
            try {
                if (writeMappedByteBuffer != null) {
                    writeMappedByteBuffer.force();
                }
            } catch (Exception e) {
                PrintUtil.error("Write flush exception." + e);
            } finally {
                SleepUtil.sleep(QueueConstant.ASYNC_FLUSH_SECS);
            }
        }
    }

    public void stop() {
        stopped = true;
        writerTask.shutdownNow();
        writeMappedByteBuffer.force();
        try {
            writeFileChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Close write channel failed.", e);
        }
    }
}
