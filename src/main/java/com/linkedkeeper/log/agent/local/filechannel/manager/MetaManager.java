package com.linkedkeeper.log.agent.local.filechannel.manager;

import com.linkedkeeper.log.agent.config.QueueConstant;
import com.linkedkeeper.log.agent.config.SenderConfig;
import com.linkedkeeper.log.agent.local.filechannel.Meta;
import com.linkedkeeper.log.agent.utils.CheckSum;
import com.linkedkeeper.log.agent.utils.PrintUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MetaManager {

    private final Meta meta;
    private final String metaPath;
    private final String metaFileName;

    private RandomAccessFile metaFile;
    private MappedByteBuffer mbb;

    public MetaManager(String queueName) {
        meta = new Meta();
        metaPath = SenderConfig.getInstance().getMetaPath() + "/";
        metaFileName = queueName + QueueConstant.META_FILE_SUFFIX;

        initMetaFile();
    }

    private void initMetaFile() {
        File dir = new File(metaPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Cannot create meta directory: " + metaPath);
        }

        try {
            metaFile = new RandomAccessFile(metaPath + metaFileName, "rw");
            mbb = metaFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, QueueConstant.MAX_META_LEN);
        } catch (Exception e) {
            throw new RuntimeException("Create meta file failed", e);
        }

        loadMetaData();
    }

    /**
     * .meta 文件使用16进制存储，2位是一个字节，样例如下：
     * 0000 0000 0000 053c 0000 004d 2f65 7870
     * 6f72 742f 6461 7461 2f6a 696e 676d 6169
     * 2f67 772f 6461 7461 436f 6c6c 6563 7469
     * 6f6e 2f64 6174 6143 6f6c 6c65 6374 6f72
     * 2e64 6174 612e 3030 3030 3030 3030 3030
     * 3030 3030 3030 3030 3200 0000 008a af1a
     * 4b00 0000 0000 0000 0000 0000 0000 0000
     * <p/>
     * mbb.getLong -> 0000 0000 0000 053c (十六进制转十进制）1340
     * mbb.getInt() -> 0000 004d (十六进制转十进制) 77
     * byte[] dst = new byte[len]; mbb.get(dst) -> 读取77个字节，转成字符串
     */
    private void loadMetaData() {
        mbb.position(0); // 设置此缓冲区的位置。如果标记已定义且大于新的位置，则丢弃该标记。
        int pos = (int) mbb.getLong(); // 读取此缓冲区的当前位置之后的 8 个字节，根据当前的字节顺序将它们组成 long 值，然后将该位置增加 8。
        int len = mbb.getInt(); // 读取此缓冲区的当前位置之后的 4 个字节，根据当前的字节顺序将它们组成 int 值，然后将该位置增加 4。
        if (len > QueueConstant.MAX_META_LEN - 20) {
            PrintUtil.error("Incorrect meta content, reset it.");
            meta.set(0, null);
        }
        byte[] dst = new byte[len];
        try {
            mbb.get(dst); // 此方法将此缓冲区的字节传输到给定的目标数组中。调用此方法的形式为 src.get(a) --> get(dst, 0, dst.length);
        } catch (BufferUnderflowException e) {
            PrintUtil.error("Incorrect meta content, reset it.");
            meta.set(0, null);
            return;
        }
        long ck = mbb.getLong();
        String name = new String(dst, Charset.forName(QueueConstant.UTF_8));
        checkValidateMeta(pos, name, ck);
    }

    private void checkValidateMeta(int pos, String fileName, long ck) {
        long ck2 = CheckSum.adler32CheckSum(Meta.concat(pos, fileName));
        if (ck2 != ck) {
            PrintUtil.warn("Incorrect check sum value " + ck + " != " + ck2 + ", reset it.");
            meta.set(0, null);
        } else {
            meta.set(pos, fileName);
        }
    }

    public Meta get() {
        return meta;
    }

    public synchronized void update(int pos, String fileName) {
        meta.set(pos, fileName);
        mbb.position(0);

        byte[] bytes = fileName.getBytes(Charset.forName(QueueConstant.UTF_8));
        mbb.putLong((long) meta.getReadPos()); // 将 8 个包含给定 long 值的字节按照当前的字节顺序写入到此缓冲区的当前位置，然后将该位置增加 8。
        mbb.putInt(bytes.length); // 将 4 个包含给定 int 值的字节按照当前的字节顺序写入到此缓冲区的当前位置，然后将该位置增加 4。
        mbb.put(bytes); // 将给定的字节写入此缓冲区的当前位置，然后该位置递增。
        mbb.putLong(meta.getCheckSum());
    }

    public synchronized void close() {
        mbb.force();
        mbb = null;
        try {
            metaFile.close();
        } catch (IOException e) {
            PrintUtil.error("Close metaFile error." + e);
        }
    }
}
