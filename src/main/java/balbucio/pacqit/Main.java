package balbucio.pacqit;

import balbucio.pacqit.logger.LoggerFormat;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.compiler.ProjectBuild;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.Form;
import de.milchreis.uibooster.model.FormBuilder;

import java.awt.*;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        ArgParse parse = new ArgParse(args);
        new Main(parse);
    }

    private Logger LOGGER = Logger.getLogger("PACQIT");
    private ArgParse parse;
    private Project project;
    private ProjectBuild projectBuild;
    private UiBooster uiBooster;
    private Scanner input;

    public Main(ArgParse parse){
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggerFormat());
        LOGGER.addHandler(handler);
        this.parse = parse;
        this.input = new Scanner(System.in);
        this.uiBooster = new UiBooster();
        this.project = Project.loadProject(parse.getProjectDir());
        if(project != null) {
            this.projectBuild = new ProjectBuild(project, parse);
            projectBuild.createPath();
        }

        switch (parse.getAction()){

            case CLEAN -> {
            }
            case COMPILE -> {
                if(project == null){
                    uiBooster.showConfirmDialog(
                            "There is no project in this folder, do you want to create it now?",
                            "Project not exists",
                            () -> {
                            },
                            () -> {
                            });
                    return;
                }

                projectBuild.compileClasses();
            }
            case BUILD -> {
                if(project == null){
                    uiBooster.showConfirmDialog(
                            "There is no project in this folder, do you want to create it now?",
                            "Project not exists",
                            () -> {
                            },
                            () -> {
                            });
                    return;
                }

                projectBuild.buildProject();
            }
            case CREATE_NEW_PROJECT -> {
                if(!parse.isConsoleOnly() ){
                    if(!GraphicsEnvironment.isHeadless()){

                    } else{
                        LOGGER.severe("The device does not have GUI support, so you must create a new project via console using the --new-project command together with --console-only.\n" +
                                "Example: pacqit --console-only --new-project\n" +
                                "\n" +
                                "If you prefer, you can create a project-config.yml template using the --new-config-template command.\n" +
                                "Example: pacqit --console-only --new-config-template");
                    }
                } else {
                    project = new Project();
                    System.out.println("What is the name of the project?");
                    project.setName(input.next());
                    System.out.println("What will the project package be? (eg: org.example)");
                    project.setProjectPackage(input.next());
                    System.out.println("What will the name of the main class be? (Ex.: Main)");
                    project.setMainClass(project.getProjectPackage()+"."+input.next());
                    project.save(parse.getProjectDir());
                    System.out.println("The project has been generated, you can change its settings through the project-config.yml file or via the console.");
                }
            }
            case NONE -> {
            }
        }
    }

    public void createProjectForm(){
        project = new Project();
        Form form = uiBooster.createForm("Create project")
                .addText("What is the name of the project?")
                .addText("What will the project package be? (eg: org.example)")
                .addText("What will the name of the main class be? (Ex.: Main)")
                .show();
        project.setName(form.getByIndex(0).asString());
        project.setProjectPackage(form.getByIndex(1).asString());
        project.setMainClass(form.getByIndex(2).asString());
    }

    public void projectSettingsForm(){
        FormBuilder form = uiBooster.createForm("Project Settings");
        form.addText("Project Name:")
                .addText("Project Package:", project.getProjectPackage())
                .addText("Project Version:", project.getVersion())
                .addText("Main Class:", project.getMainClass())
                .addText("Source Path:", project.getSourcePath())
                .addText("Resource Path:", project.getResourcePath())
                .addText("Build Plugins Path:", project.getBuildPluginsPath())
                .addText("Local Libraries Path:", project.getLocalLibrariesPath())
                .addText("Output Path:", project.getOutputPath())
                .addText("Compile Path:", project.getCompílePath())
                .addText("Generated Path:", project.getGeneratedPath())
                .addCheckbox("Executable Jar?", project.isExecutableJar())
                .addText("Jar Name:", project.getJarName())
                .addText("Java Version", project.getJavaVersion())
                .addText("Java Home:", project.getJAVA_HOME())
                .addButton("Select another Java Home", () -> {
                    uiBooster.showInfoDialog("Select the folder where Java is installed. Do not select the bin folder.");
                    form.close();
                    project.setJAVA_HOME(uiBooster.showDirectorySelection().getAbsolutePath());
                    form.show();
                });
        Form f = form.show();
        project.setProjectPackage(f.getByIndex(0).asString());
        project.setVersion(f.getByIndex(1).asString());
        project.setMainClass(f.getByIndex(2).asString());
        project.setSourcePath(f.getByIndex(3).asString());
        project.setResourcePath(f.getByIndex(4).asString());
        project.setBuildPluginsPath(f.getByIndex(5).asString());
        project.setLocalLibrariesPath(f.getByIndex(6).asString());
        project.setOutputPath(f.getByIndex(7).asString());
        project.setCompílePath(f.getByIndex(8).asString());
        project.setGeneratedPath(f.getByIndex(9).asString());
        project.setExecutableJar((boolean) f.getByIndex(10).getValue());
        project.setJarName(f.getByIndex(11).asString());
        project.setJavaVersion(f.getByIndex(12).asString());
        project.setJAVA_HOME(f.getByIndex(13).asString());
    }
}