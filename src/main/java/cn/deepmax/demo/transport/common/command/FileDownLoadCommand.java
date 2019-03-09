package cn.deepmax.demo.transport.common.command;


import cn.deepmax.demo.transport.common.support.Logger;

import java.io.File;



public class FileDownLoadCommand extends AbstractCommand implements Command {

    private String fileName;

    public FileDownLoadCommand(CommandOperator operator, String fileName) {
        super(operator);
        this.fileName = fileName;
    }

    @Override
    public void action() {
        File target = findFile();
        if(target==null){
            operator.sendText(String.format("File [%s] not found .Check your spell.", fileName));
            return;
        }
        boolean isSend = operator.sendFile(target);
        if(isSend){
            Logger.log("Open writeMode, Prepare sending file "+ target.getAbsolutePath());
        }
    }


    private File findFile(){
        File f = getCurrentFile();
        for(File it:f.listFiles()){
            if(it.isFile() && it.getName().equals(fileName)){
                return it;
            }
        }
        return null;
    }

}
