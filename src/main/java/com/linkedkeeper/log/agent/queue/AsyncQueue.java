package com.linkedkeeper.log.agent.queue;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.sender.Sender;
import com.linkedkeeper.log.agent.utils.PrintUtil;
import com.linkedkeeper.log.agent.utils.SleepUtil;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class AsyncQueue implements AsyncTask {

    private final Sender sender;
    protected final String queueName;
    protected final ExecutorService sendTask;
    protected boolean stopped = false;

    /**
     * 抽象异步队列基类：‘发送消息’线程
     */
    public AsyncQueue(final Sender sender, final String queueName) {
        this.sender = sender;
        this.queueName = queueName;
        this.sendTask = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r, "Profiler-SendTask-" + queueName);
            }
        });
    }

    /**
     * get() 从具体实现类中queue获取消息的封装体，该消息由FileChannelReader添加
     *
     * @return
     */
    public abstract byte[] get();

    /**
     * add() 将消息写入本地缓存文件，对应FileChannelWriter的具体实现：(1)文件映射内存，(2)直接写入内存
     *
     * @param message
     */
    public abstract void add(String message);

    public void shutdown() {
        this.stopped = true;
        this.sendTask.shutdownNow();
        // super abstract
        stop();
    }

    public void open() {
        sendTask.submit(new SendDriver());
        // super abstract
        start();
    }

    /**
     * 1. 从本地磁盘文件加载队列消息
     * 2. 调用具体实现类发送消息
     */
    class SendDriver extends Thread {
        private final static int SEND_EXCEPTION_TIMEOUT = 2 * 1000; // 2s

        public void run() {
            PrintUtil.info("AsyncQueue, Load message from disk: " + queueName);
            while (!stopped) {
                // this abstract
                byte[] buff = get();
                if (buff.length <= 0) {
                    PrintUtil.warn("Null message get from the queue.");
                    continue;
                }

                String message;
                try {
                    message = new String(buff, QueueConstant.UTF_8);
                } catch (Exception e) {
                    PrintUtil.error("Message type conversion error occurred. " + e);
                    continue;
                }

                reliableSend(message);
            }
        }

        private void reliableSend(String message) {
            while (!stopped) {
                try {
                    String id = UUID.randomUUID().toString();
                    sender.send(id, message);
                    return;
                } catch (Exception e) {
                    PrintUtil.error("Reliable send failed. " + e);
                    SleepUtil.sleep(SEND_EXCEPTION_TIMEOUT);
                }
            }
        }
    }

    public String getQueueName() {
        return queueName;
    }
}
