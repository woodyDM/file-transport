package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.nio.core.ContentMeta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class FileContentHandler extends AbstractContentHandler  {

    private File fileContent;
    private FileOutputStream fileOutputStream;
    private FileChannel fileChannel;
    private long lastTime = 0;

    public FileContentHandler(ContentMeta contentMeta,ContentBuffer buffer ) throws IOException{
        super(contentMeta,buffer);
        this.index = 0;
        String fileName = UUID.randomUUID().toString();
        this.fileContent = new File(fileName);
        this.fileOutputStream = new FileOutputStream(fileContent);
        this.fileChannel = fileOutputStream.getChannel();
        Logger.debug("create new ContentHandler " + getContentMeta()+" with File "+fileName);
    }

    /**
     * when byteBuffer is null , socketChannel is origin state(not read)
     *
     * @param socketChannel
     * @param byteBuffer    if not null, it is flipped ,ignore channel.
     * @param key
     * @throws IOException
     */
    @Override
    public void handle(SocketChannel socketChannel, ByteBuffer byteBuffer, SelectionKey key) throws IOException {
        if(byteBuffer!=null){
            if(byteBuffer.hasRemaining()){
                int write = fileChannel.write(byteBuffer);
                index+=write;
            }
        }else{
            long needSize = getContentMeta().getContentLength() - index;
            long write = fileChannel.transferFrom(socketChannel,index, needSize);
            long now = System.currentTimeMillis();
            if(now-lastTime>1000){
                String msg = String.format("Needsize [%s].Then transfer bytes size = [%s]  %s percents with [%s]",
                        needSize,  write, getPercentage(), getSpeedString());
                Logger.log(msg);
                lastTime = now;
            }
            index+=write;
        }
        if(index==getContentMeta().getContentLength()){
            complete(key);
            getContentBuffer().reset();
        }

    }

    /**
     * @param key
     * @throws IOException
     */
    @Override
    public void complete(SelectionKey key) throws IOException {
        fileOutputStream.flush();
        Logger.debug(String.format("File [%s] download complete, cost = [%s S] size = [%s] sizeMatch = [%s] "
                        , fileContent.getAbsolutePath(),getCostSeconds(), fileContent.length(), fileContent.length()==getContentMeta().getContentLength()));
        close();
    }


    @Override
    public void close() throws IOException {
        fileChannel.close();
        fileOutputStream.close();
        fileContent = null;
    }
}
