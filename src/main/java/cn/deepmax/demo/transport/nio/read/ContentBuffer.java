package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.nio.core.AbstractNioComponent;
import cn.deepmax.demo.transport.nio.core.ContentMeta;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 粘包情况下，有bug.后面的消息不能解析出来。
 */
public class ContentBuffer implements Closeable {


    private volatile ContentMeta contentMeta;
    private ByteBuffer headerCache = ByteBuffer.allocate(Protocol.HEADER_TOTAL_LENGTH);
    private ContentHandler contentHandler;
    private AbstractNioComponent nioComponent;

    public ContentBuffer(AbstractNioComponent nioComponent ) {
        this.nioComponent = nioComponent;
    }

    public AbstractNioComponent getNioComponent() {
        return nioComponent;
    }



    /**
     * 递归
     * @param socketChannel       origin         XX
     * @param byteBuffer            null        remaining
     * @throws IOException
     */
    public void handle(SocketChannel socketChannel, ByteBuffer byteBuffer,   SelectionKey key) throws IOException{
        if(contentMeta==null){  //should parse header
            if(byteBuffer!=null && !byteBuffer.hasRemaining()){
                return;
            }
            byteBuffer = initBuffer(socketChannel, byteBuffer);
            if(byteBuffer==null){
                return;
            }
            while (contentMeta==null && byteBuffer.hasRemaining()){
                if(headerCache.position()==Protocol.HEADER_TOTAL_LENGTH){
                    headerCache.flip();
                    contentMeta = ContentMeta.parse(headerCache);
                    headerCache.clear();
                    if(contentMeta.getContentType()== Protocol.ContentType.TEXT){
                        contentHandler = new TextContentHandler(contentMeta, this, nioComponent.getTextCall());
                    }else{
                        contentHandler = new FileContentHandler(contentMeta, this);
                    }
                }else{
                    byte b = byteBuffer.get();
                    headerCache.put(b);
                }
            }
            //buffer not enought or content created
            if(!byteBuffer.hasRemaining()){
                return;
            }
        }

        Assert.notNull(contentHandler,"content handler is null.");
        contentHandler.handle(socketChannel, byteBuffer, key);

    }

    /**
     *
     * @param socketChannel
     * @param byteBuffer
     * @return flipped buffer.
     * @throws IOException
     */
    public ByteBuffer initBuffer(SocketChannel socketChannel, ByteBuffer byteBuffer) throws IOException{
        String remote = socketChannel.getRemoteAddress().toString();
        if(byteBuffer==null){
            byteBuffer = ByteBuffer.allocate(Protocol.BUFFER_SIZE);
            int read = socketChannel.read(byteBuffer);
            if(read < 0){
                Logger.debug("CloseResource" + remote);
                socketChannel.close();
                return null;
            }
            if(read ==0){
                return null;
            }
            Logger.debug("From ["+remote + "]Raw byte read :"+read);
            byteBuffer.flip();
        }
        return byteBuffer;
    }

    /**
     * reset to original. -> preparing to parse header.
     */
    public void reset(){
        headerCache.clear();
        contentHandler = null;
        contentMeta = null;
    }


    @Override
    public void close() throws IOException {
        Util.closeQuietly(contentHandler);
    }
}
