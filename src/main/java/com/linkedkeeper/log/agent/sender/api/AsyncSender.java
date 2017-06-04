package com.linkedkeeper.log.agent.sender.api;

import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.local.filechannel.FileChannelQueue;
import com.linkedkeeper.log.agent.queue.AsyncQueue;
import com.linkedkeeper.log.agent.sender.Sender;
import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncSender {

    private final ConcurrentHashMap<String, FutureTask<AsyncQueue>> queues;
    private final AtomicBoolean stopped;
    private SenderConfig config = SenderConfig.getInstance();

    public void setConfig(SenderConfig config) {
        this.config = config;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                PrintUtil.debug("Profiler is shutting down ...");
                AsyncSender.getInstance().destroy();
            }
        }, "AsyncSender-destroy"));
    }

    public AsyncSender() {
        queues = new ConcurrentHashMap<String, FutureTask<AsyncQueue>>();
        stopped = new AtomicBoolean(false);
    }

    private static class AsyncSenderHolder {
        private static final AsyncSender INSTANCE = new AsyncSender();
    }

    public static final AsyncSender getInstance() {
        return AsyncSenderHolder.INSTANCE;
    }

    public void send(Sender sender, String key, String message) throws Exception {
        if (stopped.get()) {
            throw new Exception("Async sender has been stopped.");
        }

        try {
            /**
             * FutureTask是一种可以取消的异步的计算任务。它的计算是通过Callable实现的，它等价于可以携带结果的Runnable，并且有三个状态：等待、运行和完成。
             * 完成包括所有计算以任意的方式结束，包括正常结束、取消和异常。
             * Future有个get方法而获取结果只有在计算完成时获取，否则会一直阻塞直到任务转入完成状态，然后会返回结果或者抛出异常。
             */
            FutureTask<AsyncQueue> old = queues.get(key);
            if (old == null) {
                // step.1 --> new AsyncQueueBuilder(key)
                FutureTask<AsyncQueue> ft = new FutureTask<AsyncQueue>(new AsyncQueueBuilder(sender, key)); // 异步队列构建器
                // V putIfAbsent(K key,V value)：如果不存在key对应的值，则将value以key加入Map，否则返回key对应的旧值。
                old = queues.putIfAbsent(key, ft);
                if (old == null) {
                    old = ft;
                    old.run(); // 启动AsyncQueueBuilder的运行线程
                }
            }
            /**
             * 将消息持久化到本次磁盘文件：
             * Future有个get方法而获取结果只有在计算完成时获取，否则会一直阻塞直到任务转入完成状态，然后会返回结果或者抛出异常。
             */
            AsyncQueue aq = futureGet(old);
            // step.2 --> aq.add(message)
            aq.add(message);
        } catch (Exception e) {
            if (config.isPrintExceptionStack()) {
                e.printStackTrace();
            }
            throw new Exception("Async send exception. " + e);
        }
    }

    /**
     * 异步队列构建器：从本地磁盘加载消息
     * <p/>
     * new FileChannelQueue(key); 构建本地文件管道队列
     */
    static class AsyncQueueBuilder implements Callable<AsyncQueue> {
        private Sender sender;
        private String key;

        public AsyncQueueBuilder(Sender sender, String key) {
            this.sender = sender;
            this.key = key;
        }

        /**
         * 创建Nio本地文件消息队列。
         * open() 异步发送消息
         */
        public AsyncQueue call() throws Exception {
            AsyncQueue aq = new FileChannelQueue(sender, key);
            aq.open(); // open方法将启动 write 线程和 read 线程
            return aq;
        }
    }

    private AsyncQueue futureGet(FutureTask<AsyncQueue> ft) {
        try {
            return ft.get();
        } catch (Exception e) {
            PrintUtil.error("futureGet error. " + e);
        }
        throw new RuntimeException("Cannot create AsyncQueue.");
    }

    public void destroy() {
        stopped.set(true);

        Set<String> keySet = queues.keySet();
        for (String key : keySet) {
            try {
                queues.get(key).get().shutdown();
                PrintUtil.warn("Destroy the queue " + key);
            } catch (Exception e) {
                PrintUtil.warn("Destroy the queue " + key + " failed. " + e);
                if (config.isPrintExceptionStack()) {
                    PrintUtil.error("Destroy error. " + e);
                }
            }
        }
    }
}
