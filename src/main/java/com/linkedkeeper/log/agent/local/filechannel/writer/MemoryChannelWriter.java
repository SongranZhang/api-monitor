package com.linkedkeeper.log.agent.local.filechannel.writer;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.local.filechannel.FileChannelQueue;
import com.linkedkeeper.log.agent.utils.PrintUtil;
import com.linkedkeeper.log.agent.utils.SleepUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MemoryChannelWriter extends FileChannelWriter {

    private final ExecutorService writerSwapTask;
    private final ExecutorService writerFlushTask;
    private final ArrayBlockingQueue<String> wQueue;

    private boolean stopped = false;

    public MemoryChannelWriter(FileChannelQueue fileChannelQueue) {
        super(fileChannelQueue);
        final String queueName = fileChannelQueue.getQueueName();
        this.wQueue = new ArrayBlockingQueue<String>(100000);
        this.writerSwapTask = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profiler-MemoryWriteSwapTask-" + queueName);
            }
        });
        this.writerFlushTask = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profiler-MemoryWriteFlushTask-" + queueName);
            }
        });
    }

    public void write(String message) {
        wQueue.add(message);
    }

    public void start() {
        writerSwapTask.submit(new Runnable() {
            public void run() {
                try {
                    swap();
                } catch (Throwable t) {
                    PrintUtil.error("swap() unHandle exception." + t);
                }
                PrintUtil.info("swap() stopped.");
            }
        });

        writerFlushTask.submit(new Runnable() {
            public void run() {
                try {
                    flush();
                } catch (Throwable t) {
                    PrintUtil.error("flush() unHandle exception." + t);
                }
                PrintUtil.info("flush() stopped.");
            }
        });
    }

    private void swap() {
        while (!stopped) {
            try {
                String message = null;
                try {
                    message = wQueue.take();
                } catch (InterruptedException E) {
                    if (stopped) {
                        Iterator<String> it = wQueue.iterator();
                        while (it.hasNext()) {
                            appendMessage(it.next());
                        }
                        PrintUtil.info("Service shutdown, append the remaining message. ");
                        return;
                    }
                }
                appendMessage(message);
            } catch (Exception e) {
                PrintUtil.error("writer message exception. " + e);
            }
        }
    }

    private void appendMessage(String message) {
        if (message == null || message.equals("")) {
            return;
        }

        byte[] cb = message.getBytes(Charset.forName(QueueConstant.UTF_8));
        if (cb.length == 0) {
            return;
        }

        int messageSize = 4 + cb.length;
        checkWriteChannel(messageSize);

        writeMappedByteBuffer.putInt(cb.length);
        writeMappedByteBuffer.put(cb);

        fileChannelQueue.getLatch().countDown();
    }

    private void flush() {
        while (!stopped) {
            try {
                if (writeMappedByteBuffer != null) {
                    writeMappedByteBuffer.force();
                }
            } catch (Exception e) {
                PrintUtil.error("Writer flush exception. " + e);
            } finally {
                SleepUtil.sleep(QueueConstant.ASYNC_FLUSH_SECS);
            }
        }
    }

    public void stop() {
        stopped = true;

        writerSwapTask.shutdownNow();
        writerFlushTask.shutdownNow();
        writeMappedByteBuffer.force();
        try {
            writeFileChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Close write channel failed.", e);
        }
    }
}
