package cn.deepmax.demo.transport.common.support;

public class Protocol {

    public static final String LS = "ls";
    public static final String DIR = "dir";
    public static final String HELP = "/help";
    public static final String CD = "cd";
    public static final String DOWN_FILE = "file";
    public static final String QUIT = "quit";

    public static final int HEADER_LENGTH = 8;
    public static final byte TEXT = 1;
    public static final byte FILE = 2;
    public static final int HEADER_TOTAL_LENGTH = HEADER_LENGTH + 1;
    public static final int BUFFER_SIZE = 10*1024;

    public enum ContentType{
        TEXT,
        FILE
    }



}
