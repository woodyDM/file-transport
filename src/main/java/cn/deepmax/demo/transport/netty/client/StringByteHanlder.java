package cn.deepmax.demo.transport.netty.client;

import io.netty.util.CharsetUtil;

public class StringByteHanlder extends BytesBasedByteHandler<String> {

    public StringByteHanlder(long contentSize) {
        super(contentSize);
    }

    @Override
    String wrap(byte[] bytes) {
        return new String(bytes, CharsetUtil.UTF_8);
    }
}
