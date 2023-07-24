package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class CreateImplementerCommand implements Command {
    @Override
    public String getName() {
        return "createimplementer";
    }

    @Override
    public void run(String[] args, Main app) {
        app.createImplementerForm();
        app.getProjectBuild().createPath();
    }
}
