package com.linkedkeeper.log.agent.utils;

import com.linkedkeeper.log.agent.config.QueueConstant;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.Adler32;

public class CheckSum {

    public static long adler32CheckSum(String src) {
        long cs;
        Throwable t = null;
        for (int i = 0; i < 3; i++) {
            try {
                cs = calcCheckSum(src);
                return cs;
            } catch (IOException e) {
                t = e;
                continue;
            }
        }
        throw new RuntimeException("Cannot not calc check sum: " + src, t);
    }

    private static long calcCheckSum(String src) throws IOException {
        byte[] sb = src.getBytes(Charset.forName(QueueConstant.UTF_8));
        Adler32 checksSum = new Adler32();
        checksSum.update(sb);
        return checksSum.getValue();
    }
}
