package cn.deepmax.demo.transport.common.command;

import cn.deepmax.demo.transport.common.session.Session;

import java.io.File;

public interface CommandOperator {


    boolean sendText(String text);

    boolean sendFile(File file);

    Session getSession();

}
