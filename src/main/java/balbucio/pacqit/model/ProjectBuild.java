package balbucio.pacqit.model;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.utils.ClasseUtils;
import balbucio.pacqit.utils.JarUtils;
import balbucio.pacqit.utils.PackageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@Data
public class ProjectBuild {

    public Logger BUILD_LOGGER;
    private Project project;
    private ArgParse parse;

    public ProjectBuild(Project project, ArgParse parse){
        this.project = project;
        this.parse = parse;
        BUILD_LOGGER = Logger.getLogger("BUILD "+project.getName());
    }

    private void configureLogger(){
        BUILD_LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
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
    public File getJavaHome(){
        return new File(project.getJAVA_HOME());
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
    }

    public void compileClasses(ArgParse parse){
        List<String> classes = ClasseUtils.getClassesInDirectory(getSourcePath());
        List<String> packages = PackageUtils.getPackagesInDirectory(getSourcePath());

        // process java file

        //generate compile command
        StringBuilder classpath = new StringBuilder();
        classpath.append("-cp .");
        String separator = SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC ? ":" : ";";
        packages.forEach(p -> classpath.append(separator+getSourcePath().getAbsolutePath()+"/"+p));
        JarUtils.getJars(getLocalLibrariesPath()).forEach(j -> classpath.append(separator+j));
        classpath.append(" ");

        StringBuilder classesToCompile = new StringBuilder();
        classes.forEach(c -> classesToCompile.append(" "+c));

        // create command builder
        StringBuilder command = new StringBuilder();
        command.append("javac");
        command.append(" -d "+getCompilePath().getAbsolutePath());
        command.append(" -target "+project.getJavaVersion());
        command.append(" "+classpath.toString());
        command.append(" "+classesToCompile.toString());

        // compile classes
        try {
            ProcessBuilder builder = new ProcessBuilder(command.toString());
            builder.directory(new File(project.getJAVA_HOME()));
            Process process = builder.start();
            InputStream out = process.getInputStream();
            InputStream err = process.getErrorStream();
            Scanner scanner = new Scanner(out);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void buildClasses(ArgParse parse){

    }
}
