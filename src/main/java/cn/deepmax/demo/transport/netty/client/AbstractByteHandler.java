package cn.deepmax.demo.transport.netty.client;

import cn.deepmax.demo.transport.common.support.Assert;
import io.netty.buffer.ByteBuf;



abstract public class AbstractByteHandler<T> implements ByteHandler<T> {

    protected long pos;
    protected long contentSize;
    protected volatile boolean result ;

    public AbstractByteHandler(long contentSize) {
        this.pos = 0;
        this.contentSize = contentSize;
        this.result = false;
        init();
    }


    @Override
    public T handle(ByteBuf buf) {
        if(result){
            throw new IllegalStateException("handler can used only for once.");
        }
        long canRead = buf.readableBytes();
        if(canRead<=0)
            return null;
        long need = contentSize - pos;
        if(canRead>=need){
            T r = result(buf, pos, need);
            result = true;
            return r;
        }else{
            long read = part(buf, pos, canRead);
            pos+=read;
            Assert.isTrue(pos<contentSize,"pos should < contentSize");
            return null;
        }
    }

    void init(){

    }

    abstract T result(ByteBuf buf, long pos, long needSize);


    abstract long part(ByteBuf buf, long pos, long canRead);
}
