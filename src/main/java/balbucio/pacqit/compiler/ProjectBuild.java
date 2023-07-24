package balbucio.pacqit.compiler;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.Main;
import balbucio.pacqit.logger.BuildLoggerFormat;
import balbucio.pacqit.model.Manifest;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.model.ProjectImplementer;
import balbucio.pacqit.model.ProjectModule;
import balbucio.pacqit.obfuscation.ProjectObfuscator;
import balbucio.pacqit.utils.ClasseUtils;
import balbucio.pacqit.utils.JarUtils;
import balbucio.pacqit.utils.PackageUtils;
import de.milchreis.uibooster.components.WaitingDialog;
import lombok.Data;
import lombok.SneakyThrows;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * É nesta classe onde todas as ações relacionadas a compilação e build são feitas
 */
@Data
public class ProjectBuild {

    public Logger BUILD_LOGGER;
    private Project project;
    private ArgParse parse;
    private Main app;
    private BuildLoggerFormat format;

    public ProjectBuild(Project project, ArgParse parse, Main app){
        this.project = project;
        this.parse = parse;
        this.app = app;
        configureLogger();
    }

    private void configureLogger(){
        BUILD_LOGGER = Logger.getLogger("BUILD "+project.getName());
        BUILD_LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        format = new BuildLoggerFormat(this, null, new String());
        handler.setFormatter(format);
        BUILD_LOGGER.addHandler(handler);
    }

    public File getSourcePath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getSourcePath()) : new File(project.getSourcePath());
    }
    public File getResourcePath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getResourcePath()) : new File(project.getResourcePath());
    }

    public File getBuildPluginsPath(){
        return parse.getProjectDir() != null ?  new File(parse.getProjectDir(), project.getBuildPluginsPath()) : new File(project.getBuildPluginsPath());
    }

    public File getLocalLibrariesPath() {
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getLocalLibrariesPath()) : new File(project.getLocalLibrariesPath());
    }

    public File getOutputPath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getOutputPath()) : new File(project.getOutputPath());
    }

    public File getCompilePath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getCompílePath()) : new File(project.getCompílePath());
    }

    public File getGeneratedPath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getGeneratedPath()) : new File(project.getGeneratedPath());
    }

    public File getLogsPath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getLogsPath()) : new File(project.getLogsPath());
    }

    public File getJavaHome(){
        return new File(project.getJAVA_HOME());
    }

    public File getJAR(){
        return new File(getOutputPath(), project.replace(project.getJarName())+".jar");
    }

    public File getShadedJAR(){
        return new File(getOutputPath(), project.replace(project.getJarName())+"-shaded.jar");
    }

    public void createPath(){
        if(!getSourcePath().exists()){
            getSourcePath().mkdirs();
        }
        if(!getResourcePath().exists()){
            getResourcePath().mkdirs();
        }
        if(!getBuildPluginsPath().exists()){
            getBuildPluginsPath().mkdirs();
        }
        if(!getLocalLibrariesPath().exists()){
            getLocalLibrariesPath().mkdirs();
        }
        if(!getOutputPath().exists()){
            getOutputPath().mkdirs();
        }
        if(!getGeneratedPath().exists()){
            getGeneratedPath().mkdirs();
        }
        if(!getLogsPath().exists()){
            getLogsPath().mkdirs();
        }
        app.getImplementers().forEach(i -> {
            getImplementerSourcePath(i).mkdirs();
            getImplementerResourcePath(i).mkdirs();
            getImplementerGeneratedPath(i).mkdirs();
            getImplementerCompilePath(i).mkdirs();
            getImplementerBuildPluginsPath(i).mkdirs();
            getImplementerLocalLibrariesPath(i).mkdirs();
        });

        app.getModules().forEach(m -> {
            getModuleSourcePath(m).mkdirs();
            getModuleResourcePath(m).mkdirs();
            getModuleCompilePath(m).mkdirs();
            getModuleGeneratedPath(m).mkdirs();
            getModuleLocalLibrariesPath(m).mkdirs();
            getModuleBuildPluginsPath(m).mkdirs();
        });
    }

    /**
     * MODULE PATHS
     */
    public File getModuleSourcePath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getSourcePath()) : new File(module.getModulePath()+project.getSourcePath());
    }

    public File getModuleResourcePath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getResourcePath()) : new File(module.getModulePath()+project.getResourcePath());
    }

    public File getModuleLocalLibrariesPath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getLocalLibrariesPath()) : new File(module.getModulePath()+project.getLocalLibrariesPath());
    }

    public File getModuleBuildPluginsPath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getBuildPluginsPath()) : new File(module.getModulePath()+project.getBuildPluginsPath());
    }
    
    public File getModuleCompilePath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getCompílePath()) : new File(module.getModulePath()+project.getCompílePath());
    }

    public File getModuleJAR(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getOutputPath()+project.getJarName()) : new File(module.getModulePath()+project.getOutputPath()+module.getModuleJarName());
    }

    public File getModuleGeneratedPath(ProjectModule module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getModulePath()+project.getCompílePath()) : new File(module.getModulePath()+project.getCompílePath());
    }

    /**
     * IMPLEMENTERS
     */

    public File getImplementerGeneratedPath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getCompílePath()) : new File(module.getImplementerPath()+project.getCompílePath());
    }

    public File getImplementerSourcePath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getSourcePath()) : new File(module.getImplementerPath()+project.getSourcePath());
    }

    public File getImplementerResourcePath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getResourcePath()) : new File(module.getImplementerPath()+project.getResourcePath());
    }

    public File getImplementerLocalLibrariesPath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getLocalLibrariesPath()) : new File(module.getImplementerPath()+project.getLocalLibrariesPath());
    }

    public File getImplementerBuildPluginsPath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getBuildPluginsPath()) : new File(module.getImplementerPath()+project.getBuildPluginsPath());
    }

    public File getImplementerCompilePath(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getCompílePath()) : new File(module.getImplementerPath()+project.getCompílePath());
    }

    public File getImplementerJAR(ProjectImplementer module){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), module.getImplementerPath()+project.getOutputPath()+project.getJarName()) : new File(module.getImplementerPath()+project.getOutputPath()+module.getImplementerJarName());
    }


    @SneakyThrows
    public Compiler getCompiler(String name){
        CompilerType type = CompilerType.JAVAC;
        for (CompilerType value : CompilerType.values()) {
            if(value.getName().equalsIgnoreCase(name)){
                type = value;
            }
        }
        return (Compiler) type.getCompiler()
                .getConstructor(ProjectBuild.class)
                .newInstance(this);
    }

    public ProjectObfuscator createObsfucator(){
        return new ProjectObfuscator(project, this, parse, app);
    }

    public List<ProjectModule> getModulesFromModule(ProjectModule module){
        return app.getModules().stream().filter(m -> module.getModules().contains(m.getModuleName())).collect(Collectors.toList());
    }

    public List<ProjectModule> getModulesFromImplementer(ProjectImplementer module){
        return app.getModules().stream().filter(m -> module.getModules().contains(m.getModuleName())).collect(Collectors.toList());
    }


    public List<String> getClassesFromModule(ProjectModule module){
        List<String> classes = new ArrayList<>();
        getModulesFromModule(module).forEach(m -> {
            for (String s : ClasseUtils.getClassesInDirectory(getModuleSourcePath(module))) {
                // FakeProject/Common = ""
                // FakeProject/Common/Utils = "Common"
                // FakeProject/Common/Utils/Bytecode = "Common/Utils"
                classes.add(module.getModulePath()+"/"+s);
            }
        });
        return classes;
    }

    public void compileModule(Compiler compiler, ProjectModule module){
        for(ProjectModule smodule : getModulesFromModule(module)){
            compileModule(compiler, smodule);
            if(getModuleJAR(smodule).exists()){
                BUILD_LOGGER.info(smodule.getModuleName()+"Module JAR not found, was Pacqit able to compile the module?");
                try {
                    Files.copy(getModuleJAR(smodule).toPath(), new File(getModuleLocalLibrariesPath(module), getModuleJAR(smodule).getName()).toPath());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        compiler.compile(getModuleSourcePath(module), getModuleLocalLibrariesPath(module), getModuleCompilePath(module), getJavaHome(), project.getJavaVersion());
        JarUtils.getJarFiles(getModuleLocalLibrariesPath(module)).forEach(l -> {
            try {
                JarUtils.extractJar(l, getModuleGeneratedPath(module));
                Manifest manifest = new Manifest(module.getModuleVersion(), project.getMainClass(), project.getJavaVersion());
                JarUtils.directoryToJar(getModuleJAR(module), manifest, getModuleSourcePath(module), getModuleResourcePath(module), getModuleGeneratedPath(module));
            } catch (Exception e){
                e.printStackTrace();
                BUILD_LOGGER.severe("Unable to package dependency: "+l.getName());
                app.getUiBooster().showException("An error occurred while compiling the module!", ":C", e);
            }
        });
    }

    public void compileImplementer(Compiler compiler, ProjectImplementer module){
        for(ProjectModule smodule : getModulesFromImplementer(module)){
            compileModule(compiler, smodule);
            if(getModuleJAR(smodule).exists()){
                BUILD_LOGGER.info(smodule.getModuleName()+"Module JAR not found, was Pacqit able to compile the module?");
                try {
                    Files.copy(getModuleJAR(smodule).toPath(), new File(getImplementerLocalLibrariesPath(module), getModuleJAR(smodule).getName()).toPath());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        compiler.compile(getImplementerSourcePath(module), getImplementerLocalLibrariesPath(module), getImplementerCompilePath(module), getJavaHome(), project.getJavaVersion());
        JarUtils.getJarFiles(getImplementerLocalLibrariesPath(module)).forEach(l -> {
            try {
                JarUtils.extractJar(l, getImplementerGeneratedPath(module));
                Manifest manifest = new Manifest(module.getImplementerVersion(), project.getMainClass(), project.getJavaVersion());
                JarUtils.directoryToJar(getImplementerJAR(module), manifest, getImplementerSourcePath(module), getImplementerResourcePath(module), getImplementerGeneratedPath(module));
            } catch (Exception e){
                e.printStackTrace();
                BUILD_LOGGER.severe("Unable to package dependency: "+l.getName());
                app.getUiBooster().showException("An error occurred while compiling the module!", ":C", e);
            }
        });
    }

    /**
     * Método para compilar as classes do projeto
     *
     * Este método utiliza o javac para compilar as classes
     */
    public boolean compileClasses(){
        Compiler projectCompiler = getCompiler(project.getCompilerType());
        for (String module : project.getModules()) {
            ProjectModule m = app.getProjectModule(module);
            if(m != null) {
                try {
                    compileModule(projectCompiler, m);
                    Files.copy(getModuleJAR(m).toPath(), new File(getLocalLibrariesPath(), getModuleJAR(m).getName()).toPath());
                    for (ProjectImplementer i : app.getImplementers()) {
                        Files.copy(getModuleJAR(m).toPath(), new File(getImplementerLocalLibrariesPath(i), getModuleJAR(m).getName()).toPath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        for (String implementer : project.getImplementers()) {
            ProjectImplementer i = app.getProjectImplementer(implementer);
            try{
                compileImplementer(projectCompiler, i);
                Files.copy(getImplementerJAR(i).toPath(), new File(getLocalLibrariesPath(), getImplementerJAR(i).getName()).toPath());
                File outputJar = new File(getOutputPath(), i.replace(i.getImplementerJarName())+".jar");
                Files.copy(getImplementerJAR(i).toPath(), outputJar.toPath());
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Método para criar uma nova build do projeto
     */
    public boolean buildProject(boolean gui){
        WaitingDialog dialog = null;
        long init = System.currentTimeMillis();

        if(gui){
            dialog = app.getUiBooster().showWaitingDialog("Spinning the contraptions...", "Building project");
            format.setDialog(dialog);
        }

        // compila as classes
        boolean compiled = compileClasses();
        if(!compiled){
            BUILD_LOGGER.severe("There was some problem compiling the classes, package cancelled, check previous logs.");
            if(gui){
                dialog.close();
            }
            return false;
        }

        BUILD_LOGGER.info("Unpacking dependencies...");
        JarUtils.getJarFiles(getLocalLibrariesPath()).forEach(l -> {
            try {
                JarUtils.extractJar(l, getGeneratedPath());
            } catch (Exception e){
                e.printStackTrace();
                BUILD_LOGGER.severe("Unable to package dependency: "+l.getName());
                app.getUiBooster().showException("An error occurred while compiling the project!", ":C", e);
            }
        });

        // pack jar
        BUILD_LOGGER.info("Packing the classes into a JAR...");
        File outputJar = new File(getOutputPath(), project.replace(project.getJarName())+".jar");
        File outputShadedJar = new File(getOutputPath(), project.replace(project.getJarName())+"-shaded.jar");
        try {
            boolean deleted = true;
            if(outputJar.exists()){
                deleted = outputJar.delete();
            }

            if(outputShadedJar.exists()){
                deleted = outputShadedJar.delete();
            }

            if(!deleted){
                BUILD_LOGGER.severe("Pacqit is unable to delete the previous file. Do this manually and run the build command again.");
                if(gui){
                    dialog.close();
                }
                return false;
            }

            outputJar.createNewFile();
            outputShadedJar.createNewFile();

            Manifest manifest = null;
            if(project.isExecutableJar()){
                manifest = createManifest();
            }

            JarUtils.directoryToJar(outputJar, manifest, getCompilePath(), getResourcePath());
            BUILD_LOGGER.info("Packing shaded JAR with dependencies...");
            JarUtils.directoryToJar(outputShadedJar, manifest, getCompilePath(), getResourcePath(), getGeneratedPath());
        } catch (Exception e){
            e.printStackTrace();
            BUILD_LOGGER.severe("Unable to package the classes into a JAR.");
            if(gui){
                dialog.close();
            }
            app.getUiBooster().showException("An error occurred while compiling the project!", ":C", e);
            return false;
        }

        // verifica se os JARs estão corrompidos, se eles não carregarem aqui significa que estão
        try {
            JarFile j1 = new JarFile(outputJar);
            BUILD_LOGGER.info("JarFile Original: Successfully compiled and packaged!");
            JarFile j2 = new JarFile(outputShadedJar);
            BUILD_LOGGER.info("JarFile Shaded: Successfully compiled and packaged!");
        } catch (Exception e){
            e.printStackTrace();
            BUILD_LOGGER.severe("The JARs did not pass the build check, we are going to do an extensive check on the project, please wait.");
            if(gui){
                dialog.close();
            }
            app.getUiBooster().showException("An error occurred while compiling the project!", ":C", e);
            return false;
        }

        if(gui){
            dialog.close();
        }
        long finishTime = (System.currentTimeMillis() - init);
        BUILD_LOGGER.info("Dependencies packaged successfully in "+finishTime+"ms! The JARs were created.");
        app.getUiBooster().createNotification("Compilation and build finished in "+finishTime+" ms.", "Pacqit: Build");
        return true;
    }

    public Manifest createManifest(){
        Manifest manifest = null;
        long init = System.currentTimeMillis();
        try {
            manifest = new Manifest();
            manifest.setMainClass(project.getProjectPackage() + "." + project.getMainClass());
            manifest.save(new File(getResourcePath() + "/META-INF", "MANIFEST.mf"));
        } catch (Exception e){
            e.printStackTrace();
            app.getUiBooster().showException("An error occurred while compiling the project!", ":C", e);
        }
        BUILD_LOGGER.info("Manifest generated in "+(System.currentTimeMillis() - init)+"ms!");
        return manifest;
    }

    public void clean(){
        getGeneratedPath().delete();
        getCompilePath().delete();
        BUILD_LOGGER.info("Project was cleaned!");
    }

    public boolean run(boolean gui){
        if(!getShadedJAR().exists()){
            buildProject(gui);
        }

        StringBuilder cmdFile = new StringBuilder();

        cmdFile.append("\"");
        cmdFile.append(getJavaHome().getAbsolutePath());
        cmdFile.append("/bin/java.exe");
        cmdFile.append("\" -jar \""+getShadedJAR().getAbsolutePath()+"\"");

        try {
            ProcessBuilder builder = new ProcessBuilder(cmdFile.toString());
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            process.waitFor();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            app.getUiBooster().showException("An error occurred while execute the project!", ":C", e);
            return false;
        }
    }
}
