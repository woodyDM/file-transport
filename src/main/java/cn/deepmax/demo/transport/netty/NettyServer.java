package cn.deepmax.demo.transport.netty;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.netty.server.ServerInBoundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyServer  {

    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public NettyServer(int port) {
        this.port = port;
        boss = new NioEventLoopGroup(1);
        worker = new NioEventLoopGroup();

    }

    public static void main(String[] args) {
        if(args.length==1){
            NettyServer server = new NettyServer(Integer.valueOf(args[0]));
            server.run();
        }else{
            NettyServer server = new NettyServer(8085);
            server.run();
        }
    }

    public void run()    {
        try{

            ServerBootstrap bootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Protocol.BUFFER_SIZE,
                                0,8,1,8))
                                    .addLast(new ServerInBoundHandler());
                        }
                    })
                    .bind(port).addListener(it -> {
                        if (it.isSuccess()) {
                            Logger.log("server bind success, port = " + port);
                        } else {
                            Logger.log("server bind fail.");
                        }
                    });
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Logger.log("InterruptedException channel.");
        } finally {
            close();
        }
    }



    public void close()   {
        if(boss!=null)
            boss.shutdownGracefully();
        if(worker!=null)
            worker.shutdownGracefully();
    }
}
