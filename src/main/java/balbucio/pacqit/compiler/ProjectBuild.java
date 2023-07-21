package balbucio.pacqit.compiler;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.logger.BuildLoggerFormat;
import balbucio.pacqit.model.Manifest;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.utils.ClasseUtils;
import balbucio.pacqit.utils.JarUtils;
import balbucio.pacqit.utils.PackageUtils;
import lombok.Data;
import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
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
        configureLogger();
    }

    private void configureLogger(){
        BUILD_LOGGER = Logger.getLogger("BUILD "+project.getName());
        BUILD_LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new BuildLoggerFormat(this));
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

    /**
     * Método para compilar as classes do projeto
     */
    public boolean compileClasses(){
        long init = System.currentTimeMillis();
        BUILD_LOGGER.info("Starting to compile the project.");
        List<String> classes = ClasseUtils.getClassesInDirectory(getSourcePath());
        List<String> packages = PackageUtils.getPackagesInDirectory(getSourcePath());

        // process java file
        BUILD_LOGGER.info("Formatting and checking java classes.");

        //generate compile command
        BUILD_LOGGER.info("Preparing the javac.");
        StringBuilder classpath = new StringBuilder();
        classpath.append("-cp \".");
        String separator = SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC ? ":" : ";";
        packages.forEach(p -> classpath.append(separator+getSourcePath().getAbsolutePath()+"/"+p));
        JarUtils.getJars(getLocalLibrariesPath()).forEach(j -> classpath.append(separator+j));
        classpath.append("\"");

        StringBuilder classesToCompile = new StringBuilder();
        AtomicInteger va = new AtomicInteger(0);

        classes.forEach(c -> {
            if(va.get() > 0){
                classesToCompile.append(" \""+c+"\"");
            } else{
                classesToCompile.append("\""+c+"\"");
            }
            va.incrementAndGet();
        });

        StringBuilder cmdFile = new StringBuilder();

        cmdFile.append("\"");
        cmdFile.append(getJavaHome().getAbsolutePath());
        cmdFile.append("/bin/javac.exe");
        cmdFile.append("\"");

        // create command builder
        StringBuilder command = new StringBuilder();
        command.append(cmdFile.toString());
        command.append(" -target "+project.getJavaVersion());
        if(parse.isCompileDebug()) {
            command.append(" -verbose");
        }
        command.append(" -d \""+getCompilePath().getAbsolutePath()+"\"");
        command.append(" -sourcepath \""+getSourcePath().getAbsolutePath()+"\"");
        command.append(" "+classpath.toString()+"");
        command.append(" "+classesToCompile.toString());

        // compile classes
        BUILD_LOGGER.info("Compiling and writing classes with javac...");
        try {
            String[] cmds = new String[1];
            //cmds[0] = "\"cd "+getSourcePath().getAbsolutePath()+"\"";
            cmds[0] = command.toString();
            System.out.println(cmds[0]);
            ProcessBuilder builder = new ProcessBuilder(cmds);

            if(!getJavaHome().exists()){
                BUILD_LOGGER.severe("The specified JAVA_HOME could not be found! Check your Java installation.");
                return false;
            }

            if(!new File(getJavaHome(), "bin/javac.exe").exists()){
                BUILD_LOGGER.severe("Could not find javac.exe within the specified JAVA_HOME, this is an installation problem, please reinstall Java and try again.");
                return false;
            }
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectErrorStream(true);
            builder.directory(getSourcePath());
            Process process = builder.start();

            Scanner errscanner = new Scanner(process.getErrorStream());
            while (errscanner.hasNext()){
                BUILD_LOGGER.fine(errscanner.next());
            }

            if(parse.isCompileDebug()) {
                String line;
                BufferedReader outscanner =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = outscanner.readLine()) != null)
                    BUILD_LOGGER.fine(line);
            }
            int exitcode = process.waitFor();
            BUILD_LOGGER.info("Compilation finished in "+(System.currentTimeMillis() - init)+"ms! (exit code "+exitcode+")");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            BUILD_LOGGER.severe("Could not run javac, check your Java installation!");
            return false;
        }
    }

    /**
     * Método para criar uma nova build do projeto
     */
    public boolean buildProject(){
        long init = System.currentTimeMillis();

        // compilar classes
        boolean compiled = compileClasses();
        if(!compiled){
            BUILD_LOGGER.severe("There was some problem compiling the classes, package cancelled, check previous logs.");
            return false;
        }

        // pack jar
        BUILD_LOGGER.info("Packing the classes into a JAR...");
        File outputJar;
        try {
            outputJar = JarUtils.createJar(new File(getOutputPath(), project.replace(project.getJarName())+".jar"), getCompilePath());
            JarUtils.addFolderToJar(outputJar, getResourcePath());
            if(project.isExecutableJar()){
                createManifest();
            }
        } catch (Exception e){
            e.printStackTrace();
            BUILD_LOGGER.severe("Unable to package the classes into a JAR.");
            return false;
        }

        // add deps
        BUILD_LOGGER.info("Unpacking dependencies and adding them to a shaded JAR;");
        File outputShadedJar = new File(getOutputPath(), project.replace(project.getJarName())+"-shaded.jar");
        JarUtils.getJarFiles(getLocalLibrariesPath()).forEach(l -> {
            try {
                JarUtils.combineJars(outputJar, l, outputShadedJar, getGeneratedPath());
            } catch (Exception e){
                e.printStackTrace();
                BUILD_LOGGER.severe("Unable to package dependency: "+l.getName());
            }
        });
        BUILD_LOGGER.info("Dependencies packaged successfully in "+(System.currentTimeMillis() - init)+"ms! The JARs were created.");
        return true;
    }

    public void createManifest(){
        long init = System.currentTimeMillis();
        Manifest manifest = new Manifest();
        manifest.setMainClass(project.getProjectPackage()+"."+project.getMainClass());
        manifest.save(new File(getResourcePath()+"/META-INF", "MANIFEST.mf"));
    }

    public void clean(){
        getGeneratedPath().delete();
        getCompilePath().delete();
        BUILD_LOGGER.info("Project was cleaned!");
    }
}
