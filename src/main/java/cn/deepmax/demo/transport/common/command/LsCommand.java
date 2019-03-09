package cn.deepmax.demo.transport.common.command;


import cn.deepmax.demo.transport.common.support.Logger;

import java.io.File;

public class LsCommand extends AbstractCommand {


    public LsCommand(CommandOperator operator) {
        super(operator);
    }

    @Override
    public void action() {
        operator.sendText(getText());
    }


    private String getText() {
        File file = getCurrentFile();
        return getFileInfo(file);
    }

    private String getFileInfo(File file){
        Logger.debug("getFile info of "+file.getAbsolutePath());
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        append(sb,SEP);
        append(sb,"Now path "+file.getAbsolutePath());
        for (File it: file.listFiles()){
            if(it.isDirectory()){
                sb.append("[FOLDER]\t");

            }else{
                sb.append("[ FILE ]\t");
            }
            sb.append(it.getName()).append("\t\t\t");
            if(it.isFile()){
                sb.append(getFileSizeString(it.length()));
            }
            sb.append("\n");
        }
        append(sb,SEP);
        return sb.toString();
    }


    /**
     *
     * @param size byte
     * @return
     */
    private String getFileSizeString(long size){
        if(size < 1024){
            return size+" B";
        }
        long kb = size / 1024;
        if(kb < 1024){
            return kb + " KB";
        }
        long mb = kb / 1024;
        kb = kb % 1024;
        if(mb<1024){
            return mb+" MB "+kb+" KB";
        }
        long gb = mb / 1024;
        mb %= 1024;
        return gb+" GB "+mb+" MB";

    }

}
