package cn.deepmax.demo.transport.nio;


import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.nio.core.AbstractNioComponent;
import cn.deepmax.demo.transport.nio.core.NioSession;
import cn.deepmax.demo.transport.nio.read.ContentBuffer;
import cn.deepmax.demo.transport.nio.read.ServerTextCall;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer extends AbstractNioComponent implements Runnable {


    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final int coreNumber;
    private String charSetName;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private int port;

    public NioServer(int port,String charSetName) throws IOException {
        super(charSetName);
        this.charSetName = charSetName;
        this.port = port;
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        serverSocketChannel.socket().bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        coreNumber = Runtime.getRuntime().availableProcessors();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    }

    @Override
    public void run() {
        Logger.log("start listen on port "+port);
        try{
            while(true){
                try{
                    int num = selector.select();
                    if(num==0)
                        continue;
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if(key.isAcceptable()){
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE, new NioSession(this));
                            Logger.log("New Connection from "+ socketChannel.getRemoteAddress().toString());
                        }
                        if(key.isReadable()){
                            handleReadable(key);
                        }
                        if(key.isWritable()){
                            handleWritable(key);
                        }


                    }
                }catch (Exception e){
                    Logger.err("Exception happen. Type:["+e.getClass().getName()+"] Message :"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }finally {
            Util.closeQuietly(selector);
            Util.closeQuietly(serverSocketChannel);
        }
    }

    private void handleWritable(SelectionKey key){

        SocketChannel socketChannel =(SocketChannel)key.channel();
        NioSession session = (NioSession)key.attachment();
        try{
            boolean needRegister = sendFiles(session, socketChannel);
            if(!needRegister){
                key.interestOps(SelectionKey.OP_READ);
            }else{
                key.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            }
        }catch (CancelledKeyException|IOException e){
            key.attach(null);
            Logger.debug("handleWritable "+e.getMessage());
        }
    }




    @Override
    public ContentBuffer getContentBuffer(SelectionKey key) {
        return ((NioSession)key.attachment()).getContentBuffer();
    }

    @Override
    public void closeResource(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        socketChannel.shutdownOutput();
        socketChannel.shutdownInput();
        NioSession session = (NioSession)key.attachment();
        session.close();
        Util.closeQuietly(socketChannel);
    }

    public static void main(String[] args) throws Exception {
        if(args.length==0){
            NioServer server = new NioServer(8085,"UTF-8");
            server.setTextCall(new ServerTextCall());
            server.run();
        }else{
            int port = Integer.valueOf(args[0]);
            NioServer server = new NioServer(port,"UTF-8");
            server.setTextCall(new ServerTextCall());
            server.run();
        }
    }

}
