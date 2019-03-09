package cn.deepmax.demo.transport.nio;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.nio.core.AbstractNioComponent;
import cn.deepmax.demo.transport.nio.read.ClientTextCall;
import cn.deepmax.demo.transport.nio.read.ContentBuffer;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class NioClient extends AbstractNioComponent implements Runnable, Closeable {

    private static final int BUFFER_SIZE = 1024*1024*10;    //10M缓冲区
    private Selector selector;
    private SocketChannel socketChannel;
    private CountDownLatch countDownLatch;
    private String host;
    private int port;
    private volatile boolean shouldLoop = true;

    public NioClient(CountDownLatch latch, String host, int port,String charsetName) {
        super(charsetName);
        try {
            this.countDownLatch = latch;
            this.host = host;
            this.port = port;
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void init() throws IOException {

        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(host, port));
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        Thread thread = new Thread(this);
        thread.setName("client");
        thread.setDaemon(true);
        thread.start();

    }

    public boolean isShouldLoop() {
        return shouldLoop;
    }




    @Override
    public void closeResource(SelectionKey key) throws IOException {
        Closeable closeable = (Closeable)key.attachment();
        closeable.close();
        close();
    }

    @Override
    public void close() throws IOException {
        Util.closeQuietly(selector);
        Util.closeQuietly(socketChannel);
    }

    @Override
    public void run() {
        while (shouldLoop){
            try{
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isConnectable()){
                        if(socketChannel.isConnectionPending()){
                            if(socketChannel.finishConnect()){
                                Logger.log("connect to "+host+" port:"+port+" client:" +socketChannel.getLocalAddress());
                                socketChannel.register(selector, SelectionKey.OP_READ, new ContentBuffer(this));
                                countDownLatch.countDown();
                            }else{
                                key.cancel();
                            }
                        }
                    }
                    if(key.isReadable()){
                        handleReadable(key);
                    }
                }
            }catch (ConnectException e){
                shouldLoop = false;
                Util.closeQuietly(this);
                Logger.err("Failed to connect to server "+host );
                countDownLatch.countDown();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }



    @Override
    public ContentBuffer getContentBuffer(SelectionKey key) {
        return (ContentBuffer)key.attachment();
    }


    public static void main(String[] args) throws Exception{
        String host = null;
        String port = null;
        if(args.length!=2){
            host = "localhost";
            port = "8085";
        }else{
            host = args[0];
            port = args[1];
        }
        CountDownLatch latch = new CountDownLatch(1);
        NioClient client = new NioClient(latch, host,Integer.valueOf(port),"UTF-8");
        client.setTextCall(new ClientTextCall());
        latch.await();
        if(!client.shouldLoop){
            Logger.err("main close.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        String a = null;
        while(!"quit".equalsIgnoreCase(a)){
            a = scanner.nextLine();
            if(client.shouldLoop){
                client.sendText(a, client.socketChannel);
            }else{
                break;
            }
        }
        Logger.log("closing...");
        Util.closeQuietly(client);
    }
}
