package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.nio.core.ContentMeta;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface ContentHandler extends Closeable {


    /**
     *
     * @return
     */
    ContentMeta getContentMeta();


    /**
     *
     * @return
     */
    ContentBuffer getContentBuffer();




    /**
     * when byteBuffer is null , socketChannel is origin state(not read)
     * @param socketChannel
     * @param byteBuffer   if not null, it is flipped ,ignore channel.
     * @throws IOException
     */
    void handle(SocketChannel socketChannel, ByteBuffer byteBuffer, SelectionKey key) throws IOException;


    /**
     *
     * @param key
     * @throws IOException
     */
    void complete(SelectionKey key) throws IOException;
}
