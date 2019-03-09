package cn.deepmax.demo.transport.netty;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.netty.client.ClientInBoundHandler;
import cn.deepmax.demo.transport.netty.client.ClientSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class NettyClient {

    private int port;
    private String host;
    private EventLoopGroup boss;
    private ClientSender sender;

    public NettyClient(String host, int port) {
        this.port = port;
        this.host = host;
        boss = new NioEventLoopGroup();
        sender = new ClientSender();

    }

    public static void main(String[] args) {
        if(args.length==2){
            NettyClient client = new NettyClient(args[0],Integer.valueOf(args[1]));
            client.run();
        }else{
            NettyClient client = new NettyClient("localhost",8085);
            client.run();
        }

    }

    public void run()    {
        try{

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientInBoundHandler(sender));
                        }
                    });
            ChannelFuture connect = bootstrap.connect(new InetSocketAddress(host, port));
            connect.addListener(future->{
                if(future.isSuccess()){
                    Logger.log("connect to "+host+" at port "+port+" OK");
                }else{
                    Logger.log("failed to connect to server, msg "+ future.cause().getMessage());
                }
            });
            startMainLoop();
        }finally {
            close();
        }
    }

    public void startMainLoop(){

        Scanner scanner = new Scanner(System.in);
        String cmd = null;
        while(!Protocol.QUIT.equalsIgnoreCase(cmd)){
            cmd = scanner.nextLine();
            if(sender.isInited()){
                sender.sendText(cmd);
            }
        }
        Logger.log("closing client...");
    }


    public void close()   {
        if(boss!=null)
            boss.shutdownGracefully();

    }
}
