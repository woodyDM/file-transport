package cn.deepmax.demo.transport.netty.server;

import cn.deepmax.demo.transport.common.command.CommandOperator;
import cn.deepmax.demo.transport.common.session.Session;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.netty.support.TextSender;
import cn.deepmax.demo.transport.nio.core.ContentMeta;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NettyCommandOperator implements CommandOperator {


    private ChannelHandlerContext context;

    public NettyCommandOperator(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public boolean sendText(String text) {
        TextSender.sendText(text, context);
        return true;
    }

    @Override
    public boolean sendFile(File file) {
        long len = file.length();
        byte[] header = ContentMeta.longToByte(len);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(header, new byte[]{Protocol.FILE});
        context.channel().write(byteBuf);
        FileInputStream in ;
        try {
            in = new FileInputStream(file);
            FileRegion region = new DefaultFileRegion(in.getChannel(),0,len);
            context.channel().writeAndFlush(region).addListener(res->{
                if(res.isSuccess()){
                    Logger.log("File send ok "+file.getAbsolutePath()+" len="+len+" ,closing resource.");
                    in.close();
                }else{
                    String msg = res.cause()==null ? "no cause": res.cause().getMessage();
                    Logger.log("File send failed" + msg);
                    in.close();
                }
            });
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public Session getSession() {
        return SessionUtils.getSession(context.channel());
    }
}
