package cn.deepmax.demo.transport.nio.read;

import cn.deepmax.demo.transport.common.command.Command;
import cn.deepmax.demo.transport.common.command.CommandFactory;
import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.nio.core.NioCommandOperator;

import java.nio.channels.SelectionKey;

public class ServerTextCall implements TextCall {

    @Override
    public void call(String text, SelectionKey key) {
        Logger.log(text);
        NioCommandOperator operator = new NioCommandOperator(key);
        Command command = CommandFactory.parse(text, operator);
        command.action();
    }
}
