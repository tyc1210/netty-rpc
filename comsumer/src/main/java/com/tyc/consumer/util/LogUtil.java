package com.tyc.consumer.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-03 17:20:24
 */
public class LogUtil {
    public static void log(ByteBuf buf) {
        StringBuilder sb = new StringBuilder();
        //读索引
        sb.append(" read index:").append(buf.readerIndex());
        //写索引
        sb.append(" write index:").append(buf.writerIndex());
        //容量
        sb.append(" capacity :").append(buf.capacity());
        ByteBufUtil.appendPrettyHexDump(sb, buf);
        System.out.println(sb.toString());
    }
}
