package com.linkedkeeper.log.agent.local.filechannel;

import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.local.filechannel.manager.DataFileManager;
import com.linkedkeeper.log.agent.local.filechannel.manager.MetaManager;
import com.linkedkeeper.log.agent.local.filechannel.reader.FileChannelReader;
import com.linkedkeeper.log.agent.local.filechannel.writer.FileChannelWriter;
import com.linkedkeeper.log.agent.queue.AsyncQueue;
import com.linkedkeeper.log.agent.sender.Sender;
import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class FileChannelQueue extends AsyncQueue {

    private SenderConfig config = SenderConfig.getInstance();
    private MessageWrapper lastMessage;

    private final MetaManager metaManager;
    private final DataFileManager dataFileManager;
    private final FileChannelWriter writer;
    private final FileChannelReader reader;
    private final LinkedBlockingQueue<MessageWrapper> queue;
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * 1. 创建Meta元数据管理类：从本地加载元数据文件
     * 2. 创建数据文件管理类：从本地加载数据文件
     * 3. 创建Nio文件写操作管道：支持两种方式进行文件写，(1)通过内存与文件映射，(2)写入内存缓存。通过线程将数据更新内存
     * 4. 创建Nio文件读操作管道：与FileChannelQueue通过信号量关联，进行消息读取，将读取的消息封装Wrapper添加到FileChannelQueue的queue中
     * 5. 创建Wrapper消息封装阻塞队列
     * <p/>
     * FileChannelQueue的主要方法：
     * 1. get() 从queue中获取消息的封装体，该消息由FileChannelReader添加
     * 2. add() 将消息写入本地缓存文件，对应FileChannelWriter的具体实现：(1)文件映射内存，(2)直接写入内存
     * 3. start() 启动FileChannelReader和FileChannelWriter自主应用线程
     * 4. stop() 关闭FileChannelReader和FileChannelWriter自主应用线程，并且关系元数据管理类
     *
     * @param queueName
     */
    public FileChannelQueue(Sender sender, String queueName) {
        super(sender, queueName);
        this.metaManager = new MetaManager(queueName); // 初始化.meta元数据文件
        this.dataFileManager = new DataFileManager(queueName); //  读取.data文件列表
        this.writer = FileChannelWriter.createWriter(config.getPattern(), this); // 初始化MMapChannelWriter（内存映射方式）写数据
        this.reader = new FileChannelReader(this); // 初始化线程读取数据
        this.queue = new LinkedBlockingQueue<MessageWrapper>(100000);
    }

    /**
     * get() 从queue中获取消息的封装体，该消息由FileChannelReader添加
     *
     * @return
     */
    @Override
    public byte[] get() {
        MessageWrapper messageWrapper = null;
        confirmLastMessage();
        try {
            messageWrapper = queue.take();
            lastMessage = messageWrapper;
        } catch (InterruptedException e) {
            PrintUtil.warn("Interrupted when take message from FileChannel. " + e);
        }
        return messageWrapper == null ? null : messageWrapper.getContent();
    }

    /**
     * add() 将消息写入本地缓存文件，对应FileChannelWriter的具体实现：(1)文件映射内存，(2)直接写入内存
     *
     * @param message
     */
    @Override
    public void add(String message) {
        writer.write(message);
    }

    private void confirmLastMessage() {
        if (lastMessage != null) {
            metaManager.update(lastMessage.getEndPos(), lastMessage.getCurrentFile());
            if (lastMessage.isFirstMessage()) {
                dataFileManager.deleteOlderFilers(lastMessage.getCurrentFile());
            }
        }
    }

    /**
     * start() 启动FileChannelReader和FileChannelWriter自主应用线程
     */
    public void start() {
        writer.start();
        reader.start();
    }

    /**
     * stop() 关闭FileChannelReader和FileChannelWriter自主应用线程，并且关系元数据管理类
     */
    public void stop() {
        writer.stop();
        reader.stop();

        metaManager.close();
    }

    public MetaManager getMetaManager() {
        return metaManager;
    }

    public DataFileManager getDataFileManager() {
        return dataFileManager;
    }

    public LinkedBlockingQueue<MessageWrapper> getQueue() {
        return queue;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
