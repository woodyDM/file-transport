package cn.deepmax.demo.transport.common.command;


import cn.deepmax.demo.transport.common.session.Session;
import cn.deepmax.demo.transport.common.support.Logger;

import java.io.File;


abstract public class AbstractCommand implements Command {

    protected CommandOperator operator;


    public AbstractCommand(CommandOperator operator) {
        this.operator = operator;
    }

    /**
     * append text util
     * @param sb
     * @param msg
     */
    protected static void append(StringBuilder sb,String msg){
        sb.append(msg).append("\n");
    }

    /**
     * file support
     * @return
     */
    protected File getCurrentFile(){
        Session session = operator.getSession();
        File file = session.getCurrentFile();
        if(file==null){
            file = createNewFileDir();
            session.changeFile(file);
        }
        return file;
    }

    protected void setCurrentFile(File file){
        Session session = operator.getSession();
        session.changeFile(file);
    }



    private File createNewFileDir(){
        String f = System.getProperty("user.dir");
        Logger.debug("CreateNewDir to user dir ==> "+f);
        File file = new File(f);
        return file.getAbsoluteFile();
    }



}
