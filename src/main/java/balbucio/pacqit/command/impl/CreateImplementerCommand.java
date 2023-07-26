package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;
import balbucio.pacqit.model.project.ProjectImplementer;

import java.awt.*;
import java.util.Scanner;

public class CreateImplementerCommand implements Command {
    @Override
    public String getName() {
        return "createimplementer";
    }

    @Override
    public void run(String[] args, Main app) {
        if(!app.getParse().isConsoleOnly() && !GraphicsEnvironment.isHeadless()) {
            app.createImplementerForm();
            app.getProjectBuild().createPath();
        } else{
            ProjectImplementer implementer = new ProjectImplementer();
            Scanner input = app.getInput();
            System.out.println("What is the name of the Implementer?");
            String name = input.nextLine();
            System.out.println("What will the name of the main class be? (Ex.: Main)");
            String mainClass = input.nextLine();
            if(name != null && !name.isEmpty()){
                implementer.setImplementerName(name);
                implementer.setImplementerMainClass(mainClass);
                app.getImplementers().add(implementer);
                app.getProjectBuild().createPath();
                implementer.save(app.getParse().getProjectDir());
                app.getProject().getImplementers().add(implementer.getImplementerName());
                app.getProject().save(app.getParse().getProjectDir());
            }
        }
    }
}
