package balbucio.pacqit.obfuscation;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.Main;
import balbucio.pacqit.bytecode.JarLoader;
import balbucio.pacqit.bytecode.LoaderConfig;
import balbucio.pacqit.compiler.ProjectBuild;
import balbucio.pacqit.logger.ObfuscatorLoggerFormat;
import balbucio.pacqit.model.Manifest;
import balbucio.pacqit.model.project.Project;
import balbucio.pacqit.obfuscation.impl.HandlerObfuscation;
import balbucio.pacqit.utils.ClasseUtils;
import balbucio.pacqit.utils.JarUtils;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@NoArgsConstructor
public class ProjectObfuscator {

    private Logger OBFUSCATOR_LOGGER;
    private Project project;
    private ProjectBuild build;
    private ArgParse parse;
    private Main app;

    public ProjectObfuscator(Project project, ProjectBuild build, ArgParse parse, Main app) {
        this.project = project;
        this.build = build;
        this.parse = parse;
        this.app = app;
        configureLogger();
    }

    private void configureLogger(){
        OBFUSCATOR_LOGGER = Logger.getLogger("OBFUSCATOR");
        OBFUSCATOR_LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        ObfuscatorLoggerFormat format = new ObfuscatorLoggerFormat();
        handler.setFormatter(format);
        OBFUSCATOR_LOGGER.addHandler(handler);
    }

    public File getObfuscationPath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getObfuscationPath()) : new File(project.getObfuscationPath());
    }

    public File getJarObfuscated(){
        return new File(build.getOutputPath(), project.replace(project.getJarName()) +"-obfuscated.jar");
    }

    public void build(boolean gui) {
        OBFUSCATOR_LOGGER.info("The obfuscator is working on the classes.");
        getObfuscationPath().mkdirs();
        long init = System.currentTimeMillis();
        File workJar = new File(getObfuscationPath(), project.replace(project.getJarName())+"-working.jar");
        try{
            if(workJar.exists()) {
                workJar.delete();
            }
            Files.copy(build.getShadedJAR().toPath(), workJar.toPath());
            OBFUSCATOR_LOGGER.info("The shaded JAR has been cloned to be obfuscated.");
            JarLoader loader = new JarLoader(
                    workJar,
                    ClasseUtils.getClassesInDirectory(build.getSourcePath()),
                    LoaderConfig.builder()
                            .LOG_PATH(build.getLogsPath())
                            .GUI(gui)
                            .app(app)
                            .OUT_PATH(getObfuscationPath())
                            .LOAD_ALL_CLASSES(true)
                            .build());
            loader.setManipulationEvent(new HandlerObfuscation());
            loader.startLoad();
            loader.checkAndSaveAll();
            OBFUSCATOR_LOGGER.info("Classes have been modified and patched.");
            createJarObfuscated(loader.createManifest(project.getProjectPackage() + "." + project.getMainClass()));
            long finishedTime = (System.currentTimeMillis() - init);
            app.getUiBooster().createNotification("The obfuscated JAR is ready in "+finishedTime+"ms.", "Pacqit: Obfuscator");
            loader = null;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createJarObfuscated(Manifest manifest){
        try{
            JarUtils.directoryToJar(getJarObfuscated(), manifest, getObfuscationPath());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
