package cn.deepmax.demo.transport.common.support;

import java.time.LocalDateTime;

public class Logger {


    public static void log(Object obj){
        System.out.println(String.format("[%s][%s] : %s", LocalDateTime.now().toString(),Thread.currentThread().getName(), obj.toString()));
    }

    public static void debug(Object obj){
        if(true)
        System.out.println(String.format("[Debug][%s][%s] : %s",LocalDateTime.now().toString(), Thread.currentThread().getName(), obj.toString()));
    }

    public static void err(Object obj){
        if(true)
            System.err.println(String.format("[ERROR][%s][%s] : %s",LocalDateTime.now().toString(), Thread.currentThread().getName(), obj.toString()));
    }
}
