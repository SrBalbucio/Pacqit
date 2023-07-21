package balbucio.pacqit;

import balbucio.pacqit.command.CommandManager;
import balbucio.pacqit.logger.LoggerFormat;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.compiler.ProjectBuild;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.Form;
import de.milchreis.uibooster.model.FormBuilder;
import de.milchreis.uibooster.model.UiBoosterOptions;
import lombok.Data;

import java.awt.*;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@Data
public class Main {

    public static void main(String[] args) {
        ArgParse parse = new ArgParse(args);
        parse.configure();
        new Main(parse);
    }

    public Logger LOGGER = Logger.getLogger("PACQIT");
    private ArgParse parse;
    private Project project;
    private CommandManager commandManager;
    private ProjectBuild projectBuild;
    private UiBooster uiBooster;
    private Scanner input;

    public Main(ArgParse parse){
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggerFormat());
        LOGGER.addHandler(handler);
        LOGGER.info("Pacqit initialized successfully!");
        this.parse = parse;
        this.input = new Scanner(System.in);
        this.uiBooster = new UiBooster(UiBoosterOptions.Theme.DEFAULT, "/pacqit.png");
        this.commandManager = new CommandManager(this);
        this.project = Project.loadProject(parse.getProjectDir());
        if(project != null) {
            this.projectBuild = new ProjectBuild(project, parse, this);
            projectBuild.createPath();
        }

        switch (parse.getAction()){

            case CLEAN -> {
                if(project == null){
                    confirmProjectCreate();
                    return;
                }

                projectBuild.clean();
            }
            case COMPILE -> {
                if(project == null){
                    confirmProjectCreate();
                    return;
                }

                projectBuild.compileClasses();
            }
            case BUILD -> {
                if(project == null){
                    confirmProjectCreate();
                    return;
                }

                projectBuild.buildProject(false);
            }
            case CREATE_NEW_PROJECT -> {
                if(!parse.isConsoleOnly() ){
                    if(!GraphicsEnvironment.isHeadless()){
                        createProjectForm();
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
                if(!parse.isConsoleOnly() && !GraphicsEnvironment.isHeadless()){
                    openMenuForm();
                } else {
                    LOGGER.info("Pacqit is waiting for commands:");
                    System.out.print(">");
                    while (input.hasNextLine()) {
                        commandManager.resolve(input.nextLine());
                        LOGGER.info("Pacqit is waiting for commands:");
                        System.out.print(">");
                    }
                }
            }
        }
    }

    public void openMenuForm(){
        FormBuilder builder = uiBooster.createForm("Pacqit");
        if(project == null){
            builder.addLabel("There are no projects here!");
            builder.addButton("Create new Project", () -> createProjectForm());
        } else{
            builder.addLabel("Project Name: "+project.getName());
            builder.addButton("Open settings", () -> projectSettingsForm());
            builder.addButton("Open Dependencies Settings", () -> {});
            builder.addButton("Build", () -> projectBuild.buildProject(true));
            builder.addButton("Build and run", () -> {
                boolean build = projectBuild.buildProject(true);
                if(build) {
                    projectBuild.run(true);
                }
            });
            builder.addButton("Build and obfuscate", () -> {});
            builder.addButton("Convert to nasm and build", () -> {});
            builder.addButton("Clean", () -> projectBuild.clean());
            builder.setID("menu");
        }
        Form f = builder.show();
    }

    public void confirmProjectCreate(){
        uiBooster.showConfirmDialog(
                "There is no project in this folder, do you want to create it now?",
                "Project not exists",
                () -> {
                    createProjectForm();
                },
                () -> {});
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
        projectBuild = new ProjectBuild(project, parse, this);
        projectSettingsForm();
    }

    public void projectSettingsForm(){
        FormBuilder form = uiBooster.createForm("Project Settings");
        Form f = null;
        form.addText("Project Name:", project.getName())
                .addText("Project Package:", project.getProjectPackage())
                .addText("Project Version:", project.getVersion())
                .addText("Main Class:", project.getMainClass())
                .addButton("Path Configuration", () -> projectPathSettingsForm())
                .addButton("Build Configuration", () -> projectCompileSettingsForm());
        f = form.show();
        project.setName(f.getByIndex(0).asString());
        project.setProjectPackage(f.getByIndex(1).asString());
        project.setVersion(f.getByIndex(2).asString());
        project.setMainClass(f.getByIndex(3).asString());
        projectBuild.setProject(project);
        projectBuild.createPath();
        project.save(parse.getProjectDir());
    }

    public void projectPathSettingsForm(){
        Form f = uiBooster.createForm("Project Path Configuration")
                .addText("Source Path:", project.getSourcePath())
                .addText("Resource Path:", project.getResourcePath())
                .addText("Build Plugins Path:", project.getBuildPluginsPath())
                .addText("Local Libraries Path:", project.getLocalLibrariesPath())
                .addText("Output Path:", project.getOutputPath())
                .addText("Compile Path:", project.getCompílePath())
                .addText("Generated Path:", project.getGeneratedPath()).show();
        project.setSourcePath(f.getByIndex(0).asString());
        project.setResourcePath(f.getByIndex(1).asString());
        project.setBuildPluginsPath(f.getByIndex(2).asString());
        project.setLocalLibrariesPath(f.getByIndex(3).asString());
        project.setOutputPath(f.getByIndex(4).asString());
        project.setCompílePath(f.getByIndex(5).asString());
        project.setGeneratedPath(f.getByIndex(6).asString());
        project.save(parse.getProjectDir());
        projectBuild.createPath();
    }

    public void projectCompileSettingsForm(){
        Form f = uiBooster.createForm("Project Build Configuration")
                .addCheckbox("Executable Jar?", project.isExecutableJar())
                .addText("Jar Name:", project.getJarName())
                .addText("Java Version", project.getJavaVersion())
                .addText("Java Home:", project.getJAVA_HOME())
                .addButton("Select another Java Home", () -> {
                    uiBooster.showInfoDialog("Select the folder where Java is installed. Do not select the bin folder.");
                    project.setJAVA_HOME(uiBooster.showDirectorySelection().getAbsolutePath());
                    project.save(parse.getProjectDir());
                }).show();
        f.getByIndex(3).setValue(project.getJAVA_HOME());
        project.setExecutableJar((boolean) f.getByIndex(0).getValue());
        project.setJarName(f.getByIndex(1).asString());
        project.setJavaVersion(f.getByIndex(2).asString());
        project.setJAVA_HOME(f.getByIndex(3).asString());
        project.save(parse.getProjectDir());
        projectBuild.createPath();
    }
}