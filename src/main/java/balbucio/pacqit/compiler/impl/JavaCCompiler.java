package balbucio.pacqit.compiler.impl;

import balbucio.pacqit.compiler.Compiler;
import balbucio.pacqit.compiler.ProjectBuild;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.utils.ClasseUtils;
import balbucio.pacqit.utils.JarUtils;
import balbucio.pacqit.utils.PackageUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class JavaCCompiler implements Compiler {

    private ProjectBuild build;
    private Logger BUILD_LOGGER;

    public JavaCCompiler(ProjectBuild build) {
        this.build = build;
        this.BUILD_LOGGER  = build.getBUILD_LOGGER();
    }

    @Override
    public boolean compile(File source, File localLibraries, File compilePath, File javaHome, String javaVersion) {
        long init = System.currentTimeMillis();
        build.createPath();
        BUILD_LOGGER.info("Starting to compile the project.");
        // pega todos os packages e classes do seu projeto para criar o classpath
        List<String> classes = ClasseUtils.getClassesInDirectory(source);
        List<String> packages = PackageUtils.getPackagesInDirectory(source);

        if(classes.isEmpty()){
            return true;
        }

        // process java file
        BUILD_LOGGER.info("Formatting and checking java classes.");

        // gera o comando de classpath adicionando todas as dependencias e packages
        BUILD_LOGGER.info("Preparing the javac.");
        StringBuilder classpath = new StringBuilder();
        classpath.append("-cp \".");
        String separator = SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC ? ":" : ";";
        packages.forEach(p -> classpath.append(separator+source.getAbsolutePath()+"/"+p));
        JarUtils.getJars(localLibraries).forEach(j -> classpath.append(separator+j));
        classpath.append("\"");

        // cria o parametro de arquivos para compilação
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

        // cria o comando para o local do java
        StringBuilder cmdFile = new StringBuilder();
        cmdFile.append("\"");
        cmdFile.append(javaHome.getAbsolutePath());
        cmdFile.append("/bin/javac.exe");
        cmdFile.append("\"");

        // finalmente cria o comando final
        StringBuilder command = new StringBuilder();
        command.append(cmdFile.toString());
        command.append(" -target "+javaVersion);
        if(build.getParse().isCompileDebug()) {
            command.append(" -verbose");
        }
        command.append(" -d \""+compilePath.getAbsolutePath()+"\"");
        command.append(" -sourcepath \""+source.getAbsolutePath()+"\"");
        command.append(" "+classpath.toString()+"");
        command.append(" "+classesToCompile.toString());

        // compile classes
        BUILD_LOGGER.info("Compiling and writing classes with javac...");
        try {
            ProcessBuilder builder = new ProcessBuilder(command.toString());

            if(!javaHome.exists()){
                BUILD_LOGGER.severe("The specified JAVA_HOME could not be found! Check your Java installation.");
                return false;
            }

            if(!new File(javaHome, "bin/javac.exe").exists()){
                BUILD_LOGGER.severe("Could not find javac.exe within the specified JAVA_HOME, this is an installation problem, please reinstall Java and try again.");
                return false;
            }

            // configura o processo
            builder.redirectOutput(build.getParse().isCompileDebug() ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.DISCARD);
            builder.redirectErrorStream(true);
            builder.directory(source);
            Process process = builder.start();
            int exitcode = process.waitFor();
            BUILD_LOGGER.info("Compilation finished in "+(System.currentTimeMillis() - init)+"ms! (exit code "+exitcode+")");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            BUILD_LOGGER.severe("Could not run javac, check your Java installation!");
            return false;
        }
    }
}
