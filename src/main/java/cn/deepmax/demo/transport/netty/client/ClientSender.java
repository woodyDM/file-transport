package cn.deepmax.demo.transport.netty.client;

import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.netty.support.TextSender;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSender {

    private volatile AtomicBoolean inited = new AtomicBoolean(false);
    private ChannelHandlerContext channelHandlerContext;

    public void init(ChannelHandlerContext channelHandlerContext){
        Assert.notNull(channelHandlerContext,"context is null");
        this.channelHandlerContext = channelHandlerContext;
        inited.compareAndSet(false,true);
    }

    public void sendText(String text){
        if(inited.get()){
            TextSender.sendText(text, this.channelHandlerContext);
        }else{
            Logger.debug("Failed to send , not inited.");
        }
    }

    public boolean isInited(){
        return inited.get();
    }
}
