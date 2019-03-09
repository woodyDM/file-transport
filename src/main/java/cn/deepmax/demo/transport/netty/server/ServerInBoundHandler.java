package cn.deepmax.demo.transport.netty.server;

import cn.deepmax.demo.transport.common.command.Command;
import cn.deepmax.demo.transport.common.command.CommandFactory;
import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.IOException;

/**
 * 客户端接受字符命令handler
 */
public class ServerInBoundHandler extends SimpleChannelInboundHandler<ByteBuf>  {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Logger.log("Receive connection from "+ctx.channel().remoteAddress());
        SessionUtils.addNewSession(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte type = msg.readByte();
        Assert.isTrue(type==Protocol.TEXT," content is text.");
        int len = msg.readableBytes();
        String cmd = msg.toString(1, len, CharsetUtil.UTF_8);
        NettyCommandOperator operator = new NettyCommandOperator(ctx);
        Command command = CommandFactory.parse(cmd, operator);
        command.action();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Logger.log("client quit .."+ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof IOException){
            Logger.log(cause.getMessage());
        }else{
            cause.printStackTrace();
        }
    }
}
