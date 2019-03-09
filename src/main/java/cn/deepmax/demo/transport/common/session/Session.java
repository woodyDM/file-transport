package cn.deepmax.demo.transport.common.session;

import java.io.File;

public interface Session {

    File getCurrentFile();

    void changeFile(File file);

}
