package balbucio.pacqit.obfuscation;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.Main;
import balbucio.pacqit.bytecode.JarLoader;
import balbucio.pacqit.bytecode.LoaderConfig;
import balbucio.pacqit.compiler.ProjectBuild;
import balbucio.pacqit.model.Project;
import balbucio.pacqit.obfuscation.impl.HandlerObfuscation;
import balbucio.pacqit.utils.ClasseUtils;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Files;

@NoArgsConstructor
public class ProjectObfuscator {

    private Project project;
    private ProjectBuild build;
    private ArgParse parse;
    private Main app;

    public ProjectObfuscator(Project project, ProjectBuild build, ArgParse parse, Main app) {
        this.project = project;
        this.build = build;
        this.parse = parse;
        this.app = app;
    }

    public File getObfuscationPath(){
        return parse.getProjectDir() != null ? new File(parse.getProjectDir(), project.getObfuscationPath()) : new File(project.getObfuscationPath());
    }

    public void build(boolean gui) {
        getObfuscationPath().mkdirs();
        long init = System.currentTimeMillis();
        File workJar = new File(getObfuscationPath(), project.replace(project.getJarName())+"-working.jar");
        try{
            if(workJar.exists()) {
                workJar.delete();
            }
            Files.copy(build.getShadedJAR().toPath(), workJar.toPath());
            JarLoader loader = new JarLoader(
                    workJar,
                    ClasseUtils.getClassNames(build.getSourcePath()),
                    LoaderConfig.builder()
                            .LOG_PATH(build.getLogsPath())
                            .GUI(gui)
                            .app(app)
                            .LOAD_ALL_CLASSES(true)
                            .build());
            loader.setManipulationEvent(new HandlerObfuscation());
            loader.startLoad();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
