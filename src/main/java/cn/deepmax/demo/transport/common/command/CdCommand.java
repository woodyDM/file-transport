package cn.deepmax.demo.transport.common.command;

import java.io.File;

public class CdCommand extends AbstractCommand {


    /**
     * file
     */
    private String path;

    public CdCommand(CommandOperator operator, String path) {
        super(operator);
        this.path = path;
    }



    @Override
    public void action() {
        operator.sendText(getText());
    }

    private String getText() {
        File file = getCurrentFile();
        if(path.equals("..")){
            File parrent = file.getParentFile();
            if(parrent!=null){
                setCurrentFile(file.getParentFile());
            }
        }else{
            for (File it:file.listFiles()){
                if(it.getName().equals(path) && it.isDirectory()){
                    setCurrentFile(it);
                    break;
                }
            }
        }
        return "Now path "+getCurrentFile().getAbsolutePath();
    }



}
