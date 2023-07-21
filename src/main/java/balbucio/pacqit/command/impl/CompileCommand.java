package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class CompileCommand implements Command {
    @Override
    public String getName() {
        return "compile";
    }

    @Override
    public void run(String[] args, Main app) {
        app.getProjectBuild().compileClasses();
    }
}
