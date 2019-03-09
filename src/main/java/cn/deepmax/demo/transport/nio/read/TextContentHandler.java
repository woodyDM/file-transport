package cn.deepmax.demo.transport.nio.read;


import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.nio.core.ContentMeta;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TextContentHandler extends AbstractContentHandler{



    private ByteBuffer content;
    private TextCall textCall;

    public TextContentHandler(ContentMeta contentMeta,ContentBuffer buffer,TextCall textCall ) {
        super(contentMeta, buffer);
        this.textCall = textCall;
        this.index = 0;
        content = ByteBuffer.allocate((int)getContentMeta().getContentLength());
        Logger.debug("create new TextContentHandler "+getContentMeta());
    }


    /**
     * when byteBuffer is null , socketChannel is origin state(not read)
     *
     * @param socketChannel
     * @param byteBuffer    if not null, it is flipped ,ignore channel.
     * @throws IOException
     */
    @Override
    public void handle(SocketChannel socketChannel, ByteBuffer byteBuffer, SelectionKey key) throws IOException {
        byteBuffer = getContentBuffer().initBuffer(socketChannel, byteBuffer);
        if(byteBuffer==null){
            return;
        }
        while (byteBuffer.hasRemaining()){
            long contentNeedSize = getContentMeta().getContentLength() - index;
            Logger.debug("byte remaining:"+byteBuffer.remaining() + " needSize "+ contentNeedSize);
            if(byteBuffer.remaining() >= contentNeedSize){
                byte[] left = new byte[(int)(contentNeedSize)];
                byteBuffer.get(left);
                content.put(left);
                complete(key);
                this.getContentBuffer().handle(socketChannel, byteBuffer, key);  //ok then parse header.
            }else{
                index+=byteBuffer.remaining();
                content.put(byteBuffer);
            }
        }

    }

    /**
     * @param key
     * @throws IOException
     */
    @Override
    public void complete(SelectionKey key) throws IOException {
        content.flip();
        String string = Util.getString(content, "UTF-8");
        Logger.debug("New content ok, resetAll--->["+string+"] ");
        getContentBuffer().reset();
        textCall.call(string, key);
    }


    @Override
    public void close() throws IOException {
        if(content!=null){
            content.clear();
        }
    }
}
