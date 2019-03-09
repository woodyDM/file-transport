package cn.deepmax.demo.transport.netty.client;

import cn.deepmax.demo.transport.nio.core.ContentMeta;

public class HeaderByteHandler extends BytesBasedByteHandler<ContentMeta> {


    public HeaderByteHandler(long contentSize) {
        super(contentSize);
    }

    @Override
    ContentMeta wrap(byte[] bytes) {
        return ContentMeta.parse(bytes);
    }

}
