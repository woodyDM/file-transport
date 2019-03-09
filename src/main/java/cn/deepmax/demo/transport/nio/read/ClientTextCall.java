package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.common.support.Logger;

import java.nio.channels.SelectionKey;

public class ClientTextCall implements TextCall {

    @Override
    public void call(String text, SelectionKey key) {
        Logger.log(text);
    }
}
