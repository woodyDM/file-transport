package cn.deepmax.demo.transport.netty.support;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.nio.core.ContentMeta;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

public class TextSender {

    public static void sendText(String text, ChannelHandlerContext context){

        byte[] content = text.getBytes(CharsetUtil.UTF_8);
        long len = content.length;
        byte[] header = ContentMeta.longToByte(len);
        ByteBuf headerBuf = Unpooled.wrappedBuffer(header, new byte[]{Protocol.TEXT});
        ByteBuf contentBuf = Unpooled.wrappedBuffer(content);
        context.write(headerBuf);
        context.write(contentBuf);
        Logger.debug("Send text to "+context.channel().remoteAddress()+" with length "+len);
        context.flush();
    }
}
