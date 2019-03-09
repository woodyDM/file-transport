package cn.deepmax.demo.transport.nio.core;

import cn.deepmax.demo.transport.common.session.Session;
import cn.deepmax.demo.transport.common.support.Assert;
import cn.deepmax.demo.transport.nio.read.ContentBuffer;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NioSession implements Closeable, Session {


    private ContentBuffer contentBuffer ;
    private File currentFile ;
    private BlockingQueue<Task> downLoadFileTask = new ArrayBlockingQueue<>(10);
    private AbstractNioComponent nioComponent;

    public NioSession(AbstractNioComponent abstractNioComponent) {
        this.nioComponent = abstractNioComponent;
        contentBuffer = new ContentBuffer(abstractNioComponent);
    }


    @Override
    public void changeFile(File file) {
        Assert.notNull(file, "file is null.");
        this.currentFile = file;
    }

    @Override
    public void close() throws IOException {
        Util.closeQuietly(contentBuffer);
        downLoadFileTask.clear();
    }

    public AbstractNioComponent getNioComponent() {
        return nioComponent;
    }

    public ContentBuffer getContentBuffer() {
        return contentBuffer;
    }

    public BlockingQueue<Task> getDownLoadFileTask() {
        return downLoadFileTask;
    }

    @Override
    public File getCurrentFile() {
        return currentFile;
    }

}
