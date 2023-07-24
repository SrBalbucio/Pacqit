package balbucio.pacqit;

import balbucio.pacqit.command.CommandManager;
import balbucio.pacqit.compiler.CompilerType;
import balbucio.pacqit.logger.LoggerFormat;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.compiler.ProjectBuild;
import balbucio.pacqit.model.ProjectImplementer;
import balbucio.pacqit.model.ProjectModule;
import balbucio.pacqit.obfuscation.ProjectObfuscator;
import balbucio.pacqit.page.MainPage;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.Form;
import de.milchreis.uibooster.model.FormBuilder;
import de.milchreis.uibooster.model.UiBoosterOptions;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private List<ProjectModule> modules;
    private List<ProjectImplementer> implementers;
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
            this.modules = ProjectModule.getModulesInPath(parse.getProjectDir());
            this.implementers = ProjectImplementer.getImplementersInPath(parse.getProjectDir());
            this.projectBuild = new ProjectBuild(project, parse, this);
            projectBuild.createPath();
        }
        parse.runAction(this);
    }

    public void openMenuForm(){
        if(project == null){
            FormBuilder builder = uiBooster.createForm("Pacqit");
            builder.addLabel("There are no projects here!");
            builder.addButton("Create new Project", () -> createProjectForm());
            builder.setCloseListener(e -> System.exit(0));
            Form f = builder.show();
        } else{
            new MainPage(this);
        }
    }

    public void confirmProjectCreate(){
        uiBooster.showConfirmDialog(
                "There is no project in this folder, do you want to create it now?",
                "Project not exists",
                this::createProjectForm,
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

    public void createImplementerForm(){
        ProjectImplementer implementer = new ProjectImplementer();
        Form form = uiBooster.createForm("Create Implementer")
                .addText("What is the name of the Implementer?")
                .addText("What will the name of the main class be? (Ex.: Main)")
                .show();
        implementer.setImplementerName(form.getByIndex(0).asString());
        implementer.setImplementerMainClass(form.getByIndex(1).asString());
        implementers.add(implementer);
    }

    public void projectSettingsForm(){
        FormBuilder form = uiBooster.createForm("Project Settings");
        Form f = null;
        form.addText("Project Name:", project.getName())
                .addText("Project Package:", project.getProjectPackage())
                .addText("Project Version:", project.getVersion())
                .addText("Main Class:", project.getMainClass())
                .addButton("Path Configuration", this::projectPathSettingsForm)
                .addButton("Build Configuration", this::projectCompileSettingsForm);
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
                .addText("Generated Path:", project.getGeneratedPath())
                .addText("Obsfucation Path:", project.getObfuscationPath())
                .addText("Logs Path:", project.getLogsPath())
                .show();
        project.setSourcePath(f.getByIndex(0).asString());
        project.setResourcePath(f.getByIndex(1).asString());
        project.setBuildPluginsPath(f.getByIndex(2).asString());
        project.setLocalLibrariesPath(f.getByIndex(3).asString());
        project.setOutputPath(f.getByIndex(4).asString());
        project.setCompílePath(f.getByIndex(5).asString());
        project.setGeneratedPath(f.getByIndex(6).asString());
        project.setObfuscationPath(f.getByIndex(7).asString());
        project.setLogsPath(f.getByIndex(8).asString());
        project.save(parse.getProjectDir());
        projectBuild.createPath();
    }

    public void projectCompileSettingsForm(){
        Form f = uiBooster.createForm("Project Build Configuration")
                .addCheckbox("Executable Jar?", project.isExecutableJar())
                .addLabel("Selected Compiler: "+project.getCompilerType())
                .addSelection("Available compilers:", getCompilerNames())
                .addText("Jar Name:", project.getJarName())
                .addText("Java Version", project.getJavaVersion())
                .addText("Java Home:", project.getJAVA_HOME())
                .addButton("Select another Java Home", () -> {
                    uiBooster.showInfoDialog("Select the folder where Java is installed. Do not select the bin folder.");
                    project.setJAVA_HOME(uiBooster.showDirectorySelection().getAbsolutePath());
                    project.save(parse.getProjectDir());
                })
                .addButton("Open Native Settings", this::nativeAppSettingsForm).show();
        f.getByIndex(5).setValue(project.getJAVA_HOME());
        project.setExecutableJar((boolean) f.getByIndex(0).getValue());
        project.setCompilerType(f.getByIndex(2).asString());
        project.setJarName(f.getByIndex(3).asString());
        project.setJavaVersion(f.getByIndex(4).asString());
        project.setJAVA_HOME(f.getByIndex(5).asString());
        project.save(parse.getProjectDir());
        projectBuild.createPath();
    }

    public void nativeAppSettingsForm(){
        Form f = uiBooster.createForm("Native Installer and Package Settings")
                .addCheckbox("Create installer and native packages for this project?", project.isGenerateNativePackage())
                .addLabel("Selected tool: "+project.getToolToNativePackage())
                .addSelection("Available tools:", getPackageTools())
                .addSelectionWithCheckboxes("Installers available:", getPackageNames(), project.getNativePackages())
                .show();
        project.setGenerateNativePackage((boolean) f.getByIndex(0).getValue());
        project.setToolToNativePackage(f.getByIndex(2).asString());
        project.setNativePackages((List<String>) f.getByIndex(3).getValue());
        if(project.isGenerateNativePackage()){
            JOptionPane.showMessageDialog(null, "Packit will generate a javac compiled JAR regardless of your choice of compiler " +
                    "if you decide to create a native installer and/or package.", "Warning!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public ProjectModule getProjectModule(String name){
        return modules.stream().filter(m -> m.getModuleName().equals(name)).findFirst().orElse(null);
    }

    public ProjectImplementer getProjectImplementer(String name){
        return implementers.stream().filter(m -> m.getImplementerName().equals(name)).findFirst().orElse(null);
    }

    public static List<String> getCompilerNames(){
        List<String> names = new ArrayList<>();
        for (CompilerType value : CompilerType.values()) {
            names.add(value.getName());
        }
        return names;
    }

    public static List<String> getPackageNames(){
        return Arrays.asList("EXE", "MSI", "DEB", "RPM", "PKG", "DMG", "APP-IMAGE");
    }

    public static List<String> getPackageTools(){
        return Arrays.asList("GraalVM Native", "jpackage");
    }
}