package cn.deepmax.demo.transport.netty.client;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.SpeedUtils;
import cn.deepmax.demo.transport.nio.utils.Util;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class FileByteHanlder extends AbstractByteHandler<File> {

    private File fileContent;
    private FileOutputStream fileOutputStream;
    private FileChannel fileChannel;
    private long createTime  ;
    private long lastShow;

    public FileByteHanlder(long contentSize) {
        super(contentSize);
    }

    @Override
    void init() {
        String fileName = UUID.randomUUID().toString();
        this.fileContent = new File(fileName);
        try {
            this.fileOutputStream = new FileOutputStream(fileContent);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.createTime = System.currentTimeMillis();
        this.fileChannel = fileOutputStream.getChannel();
        this.lastShow = System.currentTimeMillis();
        Logger.debug("create new FileByteHanlder len = " + contentSize+" with File "+fileName);

    }



    @Override
    File result(ByteBuf buf, long pos, long needSize)   {
        doWriteToChannel(buf, pos, (int)needSize);
        close();
        BigDecimal s = SpeedUtils.getCostSeconds(System.currentTimeMillis(), createTime);
        String speed = SpeedUtils.getSpeedString(pos, System.currentTimeMillis(), createTime);
        Logger.debug(String.format("Complete in [%s]s,  totalSpeed[%s]", s , speed));
        return fileContent;
    }

    @Override
    long part(ByteBuf buf, long pos, long canRead) {
        long now = System.currentTimeMillis();
        if(now-lastShow>1000){
            String speed = SpeedUtils.getSpeedString(pos, System.currentTimeMillis(), createTime);
            BigDecimal perc = SpeedUtils.getPercentage(pos, contentSize);
            Logger.debug(String.format("Total [%s], canReadSize[%s] [%s]percents, totalSpeed[%s]", contentSize, canRead, perc,speed));
            lastShow = now;
        }
        return doWriteToChannel(buf, pos, (int)canRead);
    }


    private long doWriteToChannel(ByteBuf buf, long pos, int size){
        int bufPos = buf.readerIndex();
        ByteBuffer byteBuffer = buf.nioBuffer(bufPos, size);
        try {
            long write =  fileChannel.write(byteBuffer, pos);
            int newIndex =(int)(bufPos+write);
            buf.readerIndex(newIndex);
            return write;
        } catch (IOException e) {
            throw new IllegalStateException("io",e);
        }
    }


    private void close(){
        Util.closeQuietly(fileChannel );
        Util.closeQuietly(fileOutputStream );
    }
}
