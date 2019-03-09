package cn.deepmax.demo.transport.nio.core;

import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Protocol;

import java.nio.ByteBuffer;

public class ContentMeta {

    /**
     * 1-8位为长度，第9位为类型
     */
    private Protocol.ContentType contentType;
    private long contentLength;
    private long createTime;

    public Protocol.ContentType getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    private ContentMeta(){}


    public static ContentMeta parse(ByteBuffer byteBuffer){
        if(byteBuffer.remaining()<Protocol.HEADER_TOTAL_LENGTH){
            throw new IllegalArgumentException("can't parse meta");
        }
        byte[] bytes = new byte[Protocol.HEADER_TOTAL_LENGTH];
        byteBuffer.get(bytes);
        return parse(bytes);
    }

    public static ContentMeta parse(byte[] bytes){
        Assert.isTrue(bytes.length==Protocol.HEADER_TOTAL_LENGTH," failed to parse contentMeta");
        long value = byteToLong(bytes);
        ContentMeta result = new ContentMeta();
        result.contentLength = value;
        byte t = bytes[Protocol.HEADER_LENGTH];
        if(t==Protocol.FILE){
            result.contentType = Protocol.ContentType.FILE;
        }else if(t==Protocol.TEXT){
            result.contentType = Protocol.ContentType.TEXT;
        }else{
            throw new IllegalArgumentException("invalid type at pos 8 "+ t);
        }
        result.createTime = System.currentTimeMillis();
        return result;
    }




    @Override
    public String toString() {
        return "ContentMeta{" +
                "contentType=" + contentType +
                ", contentLength=" + contentLength +
                '}';
    }

    public long getCreateTime() {
        return createTime;
    }

    /**
     * long转为byte
     * @param l
     * @return
     */
    public static byte[] longToByte(long l){
        byte[] result = new byte[Protocol.HEADER_LENGTH];
        for (int i = 0; i < Protocol.HEADER_LENGTH; i++) {
            result[7-i] = (byte)((l>>i*8) & 0xFF);
        }
        return result;
    }

    /**
     * 取前8位，转化为long
     * @param bytes
     * @return
     */
    public static long byteToLong(byte[] bytes){
        int len = bytes.length;
        if(len<=Protocol.HEADER_LENGTH){
            throw new IllegalArgumentException("length greater than 8");
        }
        long value = 0;
        for (int i = 0; i < Protocol.HEADER_LENGTH; i++) {
            value<<=8;
            value|=(bytes[i] & 0xFF);
        }
        return value;
    }




}
