package cn.deepmax.demo.transport.nio.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Util {

    public static void closeQuietly(Closeable closeable){
        try {
            if(closeable!=null){
                closeable.close();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }


    public static String getString(ByteBuffer byteBuffer, String charSetName){

        Charset charset = Charset.forName(charSetName);
        CharsetDecoder charsetDecoder = charset.newDecoder();

        try {
            return charsetDecoder.decode(byteBuffer.asReadOnlyBuffer()).toString();
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static File getUserDir(){
        String path = System.getProperty("user.dir");
        return new File(path);
    }
}
