package balbucio.pacqit.command;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.impl.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private Main app;
    private List<Command> commandList = new ArrayList<>();

    public CommandManager(Main app){
        this.app = app;
        commandList.add(new OpenSettingsCommand());
        commandList.add(new CompileCommand());
        commandList.add(new BuildCommand());
        commandList.add(new ExitCommand());
        commandList.add(new CleanCommand());
        commandList.add(new RunCommand());
        commandList.add(new CreateModuleCommand());
        commandList.add(new CreateImplementerCommand());
    }

    public void resolve(String message){
        String[] args = message.split(" ");
        String[] a = new String[args.length - 1];
        System.arraycopy(args, 1, a, 0, args.length - 1);
        Command c = commandList.stream().filter(ci -> ci.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if(c != null) {
            c.run(a, app);
        } else{
           app.LOGGER.info("Command does not exist!");
        }
    }
}
