package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.common.support.SpeedUtils;
import cn.deepmax.demo.transport.nio.core.ContentMeta;

import java.math.BigDecimal;

abstract public class AbstractContentHandler implements ContentHandler {


    private ContentMeta contentMeta;
    private ContentBuffer contentBuffer;
    protected long index;

    public AbstractContentHandler(ContentMeta contentMeta, ContentBuffer contentBuffer) {
        this.contentMeta = contentMeta;
        this.contentBuffer = contentBuffer;
    }


    @Override
    public ContentBuffer getContentBuffer() {
        return contentBuffer;
    }

    @Override
    public ContentMeta getContentMeta() {
        return this.contentMeta;
    }

    /**
     *
     * @return seconds
     */
    protected BigDecimal getCostSeconds(){
        return SpeedUtils.getCostSeconds(System.currentTimeMillis(), contentMeta.getCreateTime());
    }


    /**
     *
     * @return
     */
    protected BigDecimal getPercentage(){
        return SpeedUtils.getPercentage(index, contentMeta.getContentLength());
    }




    /**
     * speed string  KB/s  MB/s
     * @return
     */
    protected String getSpeedString(){
        return SpeedUtils.getSpeedString(index, System.currentTimeMillis(), contentMeta.getCreateTime());
    }

}
