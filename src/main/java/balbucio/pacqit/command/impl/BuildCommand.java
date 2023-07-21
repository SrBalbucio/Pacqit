package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class BuildCommand implements Command {
    @Override
    public String getName() {
        return "build";
    }

    @Override
    public void run(String[] args, Main app) {
        app.getProjectBuild().buildProject();
    }
}
