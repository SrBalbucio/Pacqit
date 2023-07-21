package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class RunCommand implements Command {
    @Override
    public String getName() {
        return "run";
    }

    @Override
    public void run(String[] args, Main app) {
        app.LOGGER.info("Starting...");
        app.getProjectBuild().run();
    }
}
