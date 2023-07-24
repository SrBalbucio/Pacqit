package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class CreateModuleCommand implements Command {


    @Override
    public String getName() {
        return "createmodule";
    }

    @Override
    public void run(String[] args, Main app) {
        if(args.length > 0){
            String moduleName = args[0];
            String[] paths = moduleName.split("/");

        }
    }
}
