package cn.deepmax.demo.transport.netty.server;


import cn.deepmax.demo.transport.common.session.Session;
import cn.deepmax.demo.transport.nio.utils.Util;

import java.io.File;

public class NettySession implements Session {

    private File currentFile;

    public NettySession() {
        currentFile = Util.getUserDir();
    }

    @Override
    public void changeFile(File file) {
        currentFile = file;
    }

    @Override
    public File getCurrentFile() {
        return currentFile;
    }


}
