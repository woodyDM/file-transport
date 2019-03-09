package cn.deepmax.demo.transport.netty.server;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;



public class SessionUtils {

    private static AttributeKey<NettySession> SESSION_KEY = AttributeKey.newInstance("Session");


    public static NettySession addNewSession(Channel channel){
        Attribute<NettySession> attr = channel.attr(SESSION_KEY);
        NettySession session = new NettySession();
        attr.compareAndSet(null, session);
        return session;
    }


    public static NettySession getSession(Channel channel){
        Attribute<NettySession> attr = channel.attr(SESSION_KEY);
        return attr.get();
    }
}
