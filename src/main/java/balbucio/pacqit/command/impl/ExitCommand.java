package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class ExitCommand implements Command {
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void run(String[] args, Main app) {
        app.LOGGER.info("Bye!");
        System.exit(0);
    }
}
