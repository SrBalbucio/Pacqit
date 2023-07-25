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
import balbucio.pacqit.settings.PacqitSettings;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.Form;
import de.milchreis.uibooster.model.FormBuilder;
import de.milchreis.uibooster.model.UiBoosterOptions;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
    private PacqitSettings settings;
    private ArgParse parse;
    private Project project;
    private List<ProjectModule> modules = new ArrayList<>();
    private List<ProjectImplementer> implementers = new ArrayList<>();
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
        this.settings = PacqitSettings.getPacqitSettings();
        this.parse = parse;
        this.input = new Scanner(System.in);
        this.uiBooster = new UiBooster(UiBoosterOptions.Theme.DEFAULT, "/pacqit.png");
        this.commandManager = new CommandManager(this);
        this.project = Project.loadProject(parse.getProjectDir());
        if(project != null) {
            this.modules = ProjectModule.getModulesInPath(parse.getProjectDir() != null ? parse.getProjectDir() : new File("project-config.yml").getParentFile());
            this.implementers = ProjectImplementer.getImplementersInPath(parse.getProjectDir() != null ? parse.getProjectDir() : new File("project-config.yml").getParentFile());
            this.projectBuild = new ProjectBuild(project, parse, this);
            projectBuild.createPath();
        }
        parse.runAction(this);
        settings.setThemeInApp();
    }

    public void openMenuForm(){
        if(project == null){
            FormBuilder builder = uiBooster.createForm("Pacqit");
            builder.addLabel("There are no projects here!");
            builder.addButton("Create new Project", this::createProjectForm);
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
        if(form.getByIndex(0).asString() != null && !form.getByIndex(0).asString().isEmpty()) {
            implementer.setImplementerName(form.getByIndex(0).asString());
            implementer.setImplementerMainClass(form.getByIndex(1).asString());
            implementers.add(implementer);
            projectBuild.createPath();
            implementer.save(parse.getProjectDir());
            project.getImplementers().add(implementer.getImplementerName());
            project.save(parse.getProjectDir());
        }
    }

    public void createModuleForm(){
        ProjectModule module = new ProjectModule();
        List<String> selection = new ArrayList<>();
        selection.add("None");
        project.getModules().forEach(s -> selection.add(s));

        Form form = uiBooster.createForm("Create Module")
                .addSelection("In wich Module?", selection)
                .addText("What is the name of the Module?")
                .show();
        if(form.getByIndex(1).asString() != null && !form.getByIndex(1).asString().isEmpty()) {
            if(!form.getByIndex(0).asString().equals("None")){
                ProjectModule m = getProjectModule(form.getByIndex(0).asString());
                if(m == null){
                    return;
                }

                module.setModulePath(m.getModulePath()+"/"+m.getModuleName());
            }
            module.setModuleName(form.getByIndex(1).asString());
            modules.add(module);
            projectBuild.createPath();
            module.save(parse.getProjectDir());
            project.getModules().add(module.getModuleName());
            project.save(parse.getProjectDir());
        }
    }

    public void moduleManagerForm(){
        Form form = uiBooster.createForm("Module Manager")
                .addLabel("Number of Modules: "+getModules().size())
                .addButton("Create New Module", this::createModuleForm)
                .addButton("Configure Module", this::listModulesForm)
                .addButton("Delete Module", this::deleteModulesForm)
                .show();
    }

    public void implementerManagerForm(){
        Form form = uiBooster.createForm("Implementer Manager")
                .addLabel("Number of Implementers: "+getImplementers().size())
                .addButton("Create New Implementer", this::createImplementerForm)
                .addButton("Configure Implementer", this::listImplementerForm)
                .addButton("Delete Implementer", this::deleteImplementerForm)
                .show();
    }

    public void deleteModulesForm(){
        String selection = new UiBooster().showSelectionDialog(
                "Which module do you want to delete?",
                "Modules",
                project.getModules());
        ProjectModule module = getProjectModule(selection);
        if(module != null){
            uiBooster.showConfirmDialog("Are you sure you want to delete this module? (his files will not be deleted)",
                    "Delete Module",
                    () -> {
                        this.modules.remove(module);
                        project.getModules().remove(module.getModuleName());
                        module.delete(parse.getProjectDir());
                    },
                    () -> {});
            project.save(parse.getProjectDir());
        }
    }

    public void deleteImplementerForm(){
        String selection = new UiBooster().showSelectionDialog(
                "Which implementer do you want to delete?",
                "Implementers",
                project.getModules());
        ProjectImplementer module = getProjectImplementer(selection);
        if(module != null){
            uiBooster.showConfirmDialog("Are you sure you want to delete this implementer? (his files will not be deleted)",
                    "Delete Module",
                    () -> {
                        this.implementers.remove(module);
                        project.getImplementers().remove(module.getImplementerName());
                        module.delete(parse.getProjectDir());
                    },
                    () -> {});
            project.save(parse.getProjectDir());
        }
    }

    public void listModulesForm(){
        String selection = new UiBooster().showSelectionDialog(
                "Which module do you want to configure?",
                "Modules",
                project.getModules());
        ProjectModule module = getProjectModule(selection);
        if(module != null){
            moduleSettingsForm(module);
        }
    }

    public void listImplementerForm(){
        String selection = new UiBooster().showSelectionDialog(
                "Which implementer do you want to configure?",
                "Implementers",
                project.getImplementers());
        ProjectImplementer module = getProjectImplementer(selection);
        if(module != null){
            implementerSettingsForm(module);
        }
    }
    public void moduleSettingsForm(ProjectModule module){
        FormBuilder form = uiBooster.createForm("Module Settings");
        Form f = null;
        form.addText("Module Name:", module.getModuleName())
                .addText("Module Version:", module.getModuleVersion())
                .addText("Module Path:", module.getModulePath());
        f = form.show();
        module.setModuleName(f.getByIndex(0).asString());
        module.setModuleVersion(f.getByIndex(1).asString());
        module.setModulePath(f.getByIndex(2).asString());
        module.save(parse.getProjectDir());
    }

    public void implementerSettingsForm(ProjectImplementer module){
        FormBuilder form = uiBooster.createForm("Implementer Settings");
        Form f = null;
        form.addText("Implementer Name:", module.getImplementerName())
                .addText("Implementer Version:", module.getImplementerVersion())
                .addText("Implementer Main Class:", module.getImplementerMainClass())
                .addButton("Open native settings", () -> nativeAppImplementerSettingsForm(module));
        f = form.show();
        module.setImplementerName(f.getByIndex(0).asString());
        module.setImplementerVersion(f.getByIndex(1).asString());
        module.setImplementerMainClass(f.getByIndex(2).asString());
        module.save(parse.getProjectDir());
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

    public void nativeAppImplementerSettingsForm(ProjectImplementer implementer){
        Form f = uiBooster.createForm("Native Installer and Package Settings")
                .addCheckbox("Create installer and native packages for this implementer?", project.isGenerateNativePackage())
                .addLabel("Selected tool: "+project.getToolToNativePackage())
                .addSelection("Available tools:", getPackageTools())
                .addSelectionWithCheckboxes("Installers available:", getPackageNames(), project.getNativePackages())
                .show();
        implementer.setImplementerGenerateNativePackage((boolean) f.getByIndex(0).getValue());
        implementer.setImplementerToolNativePackage(f.getByIndex(2).asString());
        implementer.setNativePackages((List<String>) f.getByIndex(3).getValue());
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

    public boolean hasModuleInPath(String path){
        String finalPath = path.replace("\\", "/");
        return modules.stream().anyMatch(m -> (m.getModulePath()+"/"+m.getModuleName()).equals(finalPath));
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