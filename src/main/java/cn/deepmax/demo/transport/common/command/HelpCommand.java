package cn.deepmax.demo.transport.common.command;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(CommandOperator operator) {
        super(operator);
    }

    @Override
    public void action() {
        operator.sendText(getText());
    }


    private String getText() {
        return "\n" + SEP  +"\n"+
                "ls                 - list file\n" +
                "dir                - list file\n" +
                "cd [filename]      - cd \n" +
                "cd ..              - cd back to parent folder \n"+
                "file [filename]    - download file to local \n"+
                "quit               - quit me \n"+
                "/help              - show this help\n" +
               SEP + "\n";
    }
}
