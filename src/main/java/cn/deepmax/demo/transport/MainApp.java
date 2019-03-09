package cn.deepmax.demo.transport;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.netty.NettyClient;
import cn.deepmax.demo.transport.netty.NettyServer;
import cn.deepmax.demo.transport.nio.NioClient;
import cn.deepmax.demo.transport.nio.NioServer;


public class MainApp {


    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if(args.length==2){
            String mode = args[0];
            String port = args[1];
            if(isInteger(port)){
                if("-s".equalsIgnoreCase(mode)){
                    NioServer.main(new String[]{port});
                    return;
                }
                if("-sn".equalsIgnoreCase(mode)){
                    NettyServer.main(new String[]{port});
                    return;
                }
            }
        }else if(args.length==3){
            String mode = args[0];
            String serverHost = args[1];
            String serverPort = args[2];
            if(serverHost!=null && serverHost.length()>0 && isInteger(serverPort)){
                if("-c".equalsIgnoreCase(mode)){
                    NioClient.main(new String[]{serverHost, serverPort});
                    return;
                }
                if("-cn".equalsIgnoreCase(mode)){
                    NettyClient.main(new String[]{serverHost, serverPort});
                    return;
                }
            }
        }
        printInvalid();
    }



    private static boolean isInteger(String port){
        try{
            Integer.valueOf(port);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private static void printInvalid(){
        StringBuilder sb = new StringBuilder();
        append(sb,"Args invalid , Example:");
        append(sb,"[Server]\t\t\t  file-transport.jar -s port");
        append(sb,"[Client]\t\t\t  file-transport.jar -c server-ip server-port");
        append(sb,"[Netty-Server]\t\t  file-transport.jar -sn port");
        append(sb,"[Netty-Client]\t\t  file-transport.jar -cn server-ip server-port");
        Logger.err(sb.toString());
    }

    private static void append(StringBuilder sb,String msg){
        sb.append(msg).append("\n");
    }

}
