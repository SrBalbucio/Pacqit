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
        boolean run = false;
        if(args.length > 0){
            if(args[0].equalsIgnoreCase("run")){
                run = true;
            }
        }
        if(run){
            app.LOGGER.info("After compiling the application will run!");
        }
        boolean build = app.getProjectBuild().buildProject(false);
        if(build && run){
            app.getProjectBuild().run(false);
        }
    }
}
