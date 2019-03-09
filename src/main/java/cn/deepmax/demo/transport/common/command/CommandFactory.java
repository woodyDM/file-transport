package cn.deepmax.demo.transport.common.command;

import cn.deepmax.demo.transport.common.support.Logger;
import cn.deepmax.demo.transport.common.support.Protocol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandFactory {


    /**
     * ls / dir
     * @param message
     * @return
     */
    public static Command parse(String message, CommandOperator commandOperator){
        String[] st = message.split(" ");
        List<String> list = Arrays.stream(st).filter(it->it!=null && it.trim().length()!=0)
                .collect(Collectors.toList());
        Logger.debug("commandFactory parse result: "+list);
        switch (list.size()){
            case 0:
                return new InvalidCommand(commandOperator);
            case 1:{
                String cmd = list.get(0);
                if(cmd.equalsIgnoreCase(Protocol.LS)|| cmd.equalsIgnoreCase(Protocol.DIR)){
                    return new LsCommand(commandOperator);
                }else if(cmd.equalsIgnoreCase(Protocol.HELP)){
                    return new HelpCommand(commandOperator);
                }
                break;
            }
            case 2:{
                String cmd = list.get(0);
                String cmd2 = list.get(1);
                if(cmd.equalsIgnoreCase(Protocol.CD)){
                    return new CdCommand(commandOperator, cmd2);
                }else if(cmd.equals(Protocol.DOWN_FILE)){
                    return new FileDownLoadCommand(commandOperator, cmd2);
                }
                break;
            }
        }
        return new InvalidCommand(commandOperator);
    }
}
