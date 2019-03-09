package cn.deepmax.demo.transport.netty.client;

import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.nio.core.ContentMeta;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;

/**
 * 客户端接受字符命令handler
 * 假定不同消息的内容按顺序到达
 * 一个客户端只有一个此Handler，故可以有状态
 */
public class ClientInBoundHandler extends SimpleChannelInboundHandler<ByteBuf>  {


    private ClientSender sender;
    private ContentMeta contentMeta;
    private HeaderByteHandler headerByteHandler;
    private StringByteHanlder stringByteHanlder;
    private FileByteHanlder fileByteHanlder;

    public ClientInBoundHandler (ClientSender sender){
        this.sender = sender;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Logger.log("Bind sender, Connect host to "+ctx.channel().remoteAddress()+", local "+ctx.channel().localAddress());
        sender.init(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if(contentMeta==null){
            parseHeader(msg);
        }else{
            parseContent(msg);
        }
    }




    private void parseHeader(ByteBuf buf){
        if(headerByteHandler==null){
            headerByteHandler = new HeaderByteHandler(Protocol.HEADER_TOTAL_LENGTH);
        }
        ContentMeta meta = headerByteHandler.handle(buf);
        if(meta!=null){
            Logger.debug("Client contentMeta:"+meta);
            this.contentMeta = meta;
            headerByteHandler = null;
            parseContent(buf);
        }
    }

    private void parseContent(ByteBuf buf){
        Assert.isTrue(contentMeta!=null,"content is null.");
        if(contentMeta.getContentType()== Protocol.ContentType.TEXT){
            if(stringByteHanlder==null){
                stringByteHanlder = new StringByteHanlder(contentMeta.getContentLength());
            }
            parseContent0(buf, stringByteHanlder);
        }else{
            if(fileByteHanlder==null){
                fileByteHanlder = new FileByteHanlder(contentMeta.getContentLength());
            }
            parseContent0(buf, fileByteHanlder);
        }
    }


    private  <T> void parseContent0(ByteBuf buf, ByteHandler<T> handler){
        T f = handler.handle(buf);
        if(f!=null){
            receive(f);
            reset();
            parseHeader(buf);
        }
    }

    private void receive(Object obj){
        if(obj instanceof File){
            receiveFile((File)obj);
        }else{
            receiveText((String)obj);
        }
    }

    private void receiveFile(File file){
        Logger.log("File recerive ok "+file.getAbsolutePath()+" len = "+file.length());
    }


    private void receiveText(String text){
        Logger.log("Client textReceive ==>"+text);
    }

    private void reset(){
        stringByteHanlder = null;
        fileByteHanlder = null;
        contentMeta = null;
    }

}
