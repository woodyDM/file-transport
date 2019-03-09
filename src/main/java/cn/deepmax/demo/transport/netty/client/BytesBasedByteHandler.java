package cn.deepmax.demo.transport.netty.client;

import io.netty.buffer.ByteBuf;

abstract public class BytesBasedByteHandler<T> extends AbstractByteHandler<T>{


    private byte[] bytes;

    public BytesBasedByteHandler(long contentSize) {
        super(contentSize);
    }

    @Override
    void init() {
        bytes = new byte[(int)contentSize];
    }


    @Override
    T result(ByteBuf buf, long pos, long needSize) {
        buf.readBytes(bytes,(int)pos, (int)needSize);
        return wrap(bytes);
    }

    abstract T wrap(byte[] bytes);


    @Override
    long part(ByteBuf buf, long pos, long canRead) {
        buf.readBytes(bytes,(int)pos, (int)canRead);
        return canRead;
    }
}
