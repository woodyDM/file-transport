package cn.deepmax.demo.transport.nio.read;

import java.nio.channels.SelectionKey;

/**
 * action on text ok.
 */
public interface TextCall   {

    void call(String text, SelectionKey key);

}
