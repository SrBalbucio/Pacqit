package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class CleanCommand implements Command {
    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public void run(String[] args, Main app) {
        app.getProjectBuild().clean();
    }
}
