package cn.deepmax.demo.transport.common.support;

public class Assert {


    public static void notNull(Object obj, String msg){
        if(obj==null){
            throw new IllegalStateException(msg);
        }
    }

    public static void isTrue(boolean t,String msg){
        if(!t)
            throw new IllegalStateException(msg);
    }

    public static void notNull(Object obj){
        notNull(obj," the object is null.Check your code.");
    }
}
