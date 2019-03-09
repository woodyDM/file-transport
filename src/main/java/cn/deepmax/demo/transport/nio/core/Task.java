package cn.deepmax.demo.transport.nio.core;

import cn.deepmax.demo.transport.common.support.Protocol;

import java.io.File;

public class Task {

    private File targetFile;
    private long total;
    private long index;
    private int times;
    private boolean headerSend;
    private byte[] header;
    private int headerIndex;
    private long startTime;

    public Task(File targetFile) {
        this.targetFile = targetFile;
        total = targetFile.length();
        init();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public byte[] getHeader() {
        return header;
    }

    public int getHeaderIndex() {
        return headerIndex;
    }

    private void init(){
        header = new byte[Protocol.HEADER_TOTAL_LENGTH];
        byte[] left = ContentMeta.longToByte(total);
        System.arraycopy(left, 0 , header, 0, Protocol.HEADER_LENGTH);
        header[Protocol.HEADER_TOTAL_LENGTH-1] = Protocol.FILE;
        headerIndex = 0;
        headerSend = false;
        index = 0;
        times = 0;
    }

    public boolean isHeaderSend() {
        return headerSend;
    }

    public void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    public void setHeaderSend(boolean headerSend) {
        this.headerSend = headerSend;
    }

    public void incre(long incre){
        index+=incre;
        times++;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public long getTotal() {
        return total;
    }

    public long getIndex() {
        return index;
    }

    public int getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return "Task{" +
                "targetFile=" + targetFile.getAbsolutePath() +
                ", total=" + total +
                '}';
    }
}
