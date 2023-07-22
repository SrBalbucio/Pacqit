package balbucio.pacqit;

import balbucio.pacqit.model.Project;
import balbucio.pacqit.model.ProjectAction;
import lombok.Data;

import java.awt.*;
import java.io.File;

@Data
public class ArgParse {

    private String[] args;
    private boolean consoleOnly = false;
    private boolean verbose = false;
    private boolean compileDebug = false;
    private File projectDir = null;
    private ProjectAction action = ProjectAction.NONE;

    // ACTION


    public ArgParse(String[] args) {
        this.args = args;
    }

    public void configure(){
        for(String c : args){
            if(c.equalsIgnoreCase("--console-only")){
                consoleOnly = true;
            } else if(c.equalsIgnoreCase("--verbose")){
                verbose = true;
            } else if(c.equalsIgnoreCase("--debug")){
                compileDebug = true;
            } else if (c.equalsIgnoreCase("--compile")){
                action = ProjectAction.COMPILE;
            } else if(c.equalsIgnoreCase("--build")){
                action = ProjectAction.BUILD;
            } else if(c.equalsIgnoreCase("--new-project")){
                action = ProjectAction.CREATE_NEW_PROJECT;
            }else if(c.equalsIgnoreCase("--clean")){
                action = ProjectAction.CLEAN;
            }else {
                String[] option = c.split("=");
                if(option[0].equalsIgnoreCase("--projectdir")){
                    projectDir = new File(option[1]);
                }
            }
        }
    }

    public void runAction(Main app){
        switch (this.getAction()){

            case CLEAN -> {
                if(app.getProject() == null){
                    app.confirmProjectCreate();
                    return;
                }

                app.getProjectBuild().clean();
            }
            case COMPILE -> {
                if(app.getProject() == null){
                    app.confirmProjectCreate();
                    return;
                }

                app.getProjectBuild().compileClasses();
            }
            case BUILD -> {
                if(app.getProject() == null){
                    app.confirmProjectCreate();
                    return;
                }

                app.getProjectBuild().buildProject(false);
            }
            case CREATE_NEW_PROJECT -> {
                if(!this.isConsoleOnly() ){
                    if(!GraphicsEnvironment.isHeadless()){
                        app.createProjectForm();
                    } else{
                        app.LOGGER.severe("The device does not have GUI support, so you must create a new project via console using the --new-project command together with --console-only.\n" +
                                "Example: pacqit --console-only --new-project\n" +
                                "\n" +
                                "If you prefer, you can create a project-config.yml template using the --new-config-template command.\n" +
                                "Example: pacqit --console-only --new-config-template");
                    }
                } else {
                    app.setProject(new Project());
                    System.out.println("What is the name of the project?");
                    app.getProject().setName(app.getInput().next());
                    System.out.println("What will the project package be? (eg: org.example)");
                    app.getProject().setProjectPackage(app.getInput().next());
                    System.out.println("What will the name of the main class be? (Ex.: Main)");
                    app.getProject().setMainClass(app.getProject().getProjectPackage()+"."+app.getInput().next());
                    app.getProject().save(this.getProjectDir());
                    System.out.println("The project has been generated, you can change its settings through the project-config.yml file or via the console.");
                }
            }
            case NONE -> {
                if(!this.isConsoleOnly() && !GraphicsEnvironment.isHeadless()){
                    app.openMenuForm();
                } else {
                    app.LOGGER.info("Pacqit is waiting for commands:");
                    System.out.print(">");
                    while (app.getInput().hasNextLine()) {
                        app.getCommandManager().resolve(app.getInput().nextLine());
                        app.LOGGER.info("Pacqit is waiting for commands:");
                        System.out.print(">");
                    }
                }
            }
        }
    }
}
