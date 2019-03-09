package cn.deepmax.demo.transport.nio.core;

import cn.deepmax.demo.transport.common.command.CommandOperator;
import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.common.support.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NioCommandOperator implements CommandOperator {


    private SelectionKey key;

    public NioCommandOperator(SelectionKey key) {
        this.key = key;
    }

    @Override
    public NioSession getSession() {
        return (NioSession)key.attachment();
    }

    @Override
    public boolean sendText(String text) {
        try {
            getSession().getNioComponent().sendText(text, (SocketChannel)key.channel());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendFile(File file) {
        Assert.isTrue(file.isFile(),"file is not a file.");
        boolean isAdded = getSession().getNioComponent().sendFile(getSession(), file);
        if(isAdded){
            Logger.log("Open writeMode, Prepare sending file "+ file.getAbsolutePath());
            key.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        }else{
            key.interestOps(SelectionKey.OP_READ);
        }
        return isAdded;
    }
}
