package cn.deepmax.demo.transport.nio.write;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.nio.core.ContentMeta;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TextSender {

    public static int sendText(String str, SocketChannel socketChannel, Charset charset) throws IOException {
        Logger.debug("Send -->"+str);
        byte[] bytes = str.getBytes(charset);
        int totalLen = bytes.length + Protocol.HEADER_TOTAL_LENGTH;
        byte[] header = ContentMeta.longToByte((long)bytes.length);
        //1-8 header 9 type -> contents
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.put(header);
        byteBuffer.put(Protocol.TEXT);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        return socketChannel.write(byteBuffer);

    }
}
