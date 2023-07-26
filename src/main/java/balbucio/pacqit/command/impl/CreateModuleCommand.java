package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;
import balbucio.pacqit.model.project.ProjectModule;

import java.awt.*;
import java.util.Scanner;

public class CreateModuleCommand implements Command {


    @Override
    public String getName() {
        return "createmodule";
    }

    @Override
    public void run(String[] args, Main app) {
        if(args.length > 0) {
            String path = args[1];
            if (app.hasModuleInPath(path)) {
                if (!app.getParse().isConsoleOnly() && !GraphicsEnvironment.isHeadless()) {
                    app.createModuleForm();
                    app.getProjectBuild().createPath();
                } else {
                    ProjectModule module = new ProjectModule();
                    Scanner input = app.getInput();
                    System.out.println("What is the name of the Module?");
                    String name = input.nextLine();
                    if (name != null && !name.isEmpty()) {
                        module.setModuleName(name);
                        app.getModules().add(module);
                        app.getProjectBuild().createPath();
                        module.save(app.getParse().getProjectDir());
                        app.getProject().getImplementers().add(module.getModuleName());
                        app.getProject().save(app.getParse().getProjectDir());
                    }
                }
            }
        }
    }
}
