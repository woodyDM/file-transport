package cn.deepmax.demo.transport.common.command;

public class InvalidCommand extends AbstractCommand {

    public InvalidCommand(CommandOperator operator) {
        super(operator);
    }

    @Override
    public void action() {
        operator.sendText(getText());
    }


    private String getText() {
        return "Invalid command enter /help for help";
    }
}
