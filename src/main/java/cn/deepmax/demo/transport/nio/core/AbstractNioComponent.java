package cn.deepmax.demo.transport.nio.core;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;
import cn.deepmax.demo.transport.nio.read.ContentBuffer;
import cn.deepmax.demo.transport.nio.read.TextCall;
import cn.deepmax.demo.transport.nio.utils.Util;
import cn.deepmax.demo.transport.nio.write.TextSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;


abstract public class AbstractNioComponent{


    protected Charset charset;
    protected TextCall textCall;

    public TextCall getTextCall() {
        return textCall;
    }

    public void setTextCall(TextCall textCall) {
        this.textCall = textCall;
    }

    public AbstractNioComponent(String charsetName) {
        this.charset = Charset.forName(charsetName);
    }

    public int sendText(String str, SocketChannel socketChannel) throws IOException {
        return TextSender.sendText(str, socketChannel, charset);
    }

    /**
     * sendfile in queue.
     * @param session
     * @param socketChannel
     * @return
     * @throws IOException
     */
    public boolean sendFiles(NioSession session, SocketChannel socketChannel) throws IOException{
        TaskState taskState = null;
        while (true){
            taskState = sentFileWhenWriteable(session, socketChannel);
            if(taskState==TaskState.NO_TASK){
                Logger.debug("NO_TASK.");
                return false;
            }else if(taskState==TaskState.NO_BUFFER_SIZE){
                Logger.debug("NO_BUFFER_SIZE.");
                return true;
            }else{
                Logger.debug("Continue.");
            }
        }
    }

    /**
     *
     * @param session
     * @param socketChannel
     * @return
     * @throws IOException
     */
    private TaskState sentFileWhenWriteable(NioSession session, SocketChannel socketChannel) throws IOException{
        Task task = session.getDownLoadFileTask().peek();
        if(task==null){
            return TaskState.NO_TASK;
        }
        if(!task.isHeaderSend()){
            int needSendSize = Protocol.HEADER_TOTAL_LENGTH - task.getHeaderIndex();
            ByteBuffer headerBuffer = ByteBuffer.allocate(needSendSize);
            headerBuffer.put(task.getHeader(), task.getHeaderIndex(), needSendSize);
            headerBuffer.flip();
            int writeBytes = socketChannel.write(headerBuffer);
            if(writeBytes==needSendSize){
                task.setHeaderSend(true);
                Logger.debug("Send header ok --> task = "+task);
                task.setStartTime(System.currentTimeMillis());
            }else{
                task.setHeaderIndex(task.getHeaderIndex() + writeBytes);
                return TaskState.NO_BUFFER_SIZE;
            }
        }
        //send file
        long needSend = task.getTotal() - task.getIndex();
        FileInputStream fileInputStream = null;
        FileChannel channel = null;
        try{
            fileInputStream = new FileInputStream(task.getTargetFile());
            channel = fileInputStream.getChannel();
            long transed = channel.transferTo(task.getIndex(), needSend, socketChannel);
            String remote = socketChannel.getRemoteAddress().toString();
            Logger.debug(String.format("transferTo [%s]index =[%s] needSend=[%s] realSend=[%s]",remote,task.getIndex(),needSend, transed));
            if(transed<needSend){
                task.incre(transed);
                return TaskState.NO_BUFFER_SIZE;
            }else{
                session.getDownLoadFileTask().remove(task);
                Logger.log("Send file Over"+task);
                return TaskState.CONTINUE;
            }
        }finally {
            Util.closeQuietly(fileInputStream);
            Util.closeQuietly(channel);
        }
    }

    /**
     * task register
     * @param session
     * @param file
     * @return
     */
    public boolean sendFile(NioSession session, File file) {
        Task task = new Task(file);
        boolean isAdded = session.getDownLoadFileTask().offer(task);
        return isAdded;
    }

    protected void handleReadable(SelectionKey key)throws IOException{

        try{
            SocketChannel socketChannel = (SocketChannel)key.channel();
            ContentBuffer buffer = getContentBuffer(key);
            if(buffer==null){
                throw new IllegalStateException("Bug Found. ContentBuffer is null,check key and attachment.");
            }
            buffer.handle(socketChannel, null, key);
        }catch (ClosedChannelException e){
            Logger.debug("channel cancel "+e.getMessage() );
            key.cancel();
        }catch (IOException e){
            closeResource(key);
            Logger.debug("channel IOException "+e.getMessage() );
        }
    }

    abstract public ContentBuffer getContentBuffer(SelectionKey key);


    abstract public void closeResource(SelectionKey key) throws IOException;


}
