package cn.deepmax.demo.transport.netty.client;

import io.netty.buffer.ByteBuf;

public interface ByteHandler<T> {



    /**
     *

     * @param buf
     * @return      null if size not enough.
     */
    T handle(  ByteBuf buf );


}
