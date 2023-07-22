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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public void build() {
        getObfuscationPath().mkdirs();
        long init = System.currentTimeMillis();
        File workJar = new File(getObfuscationPath(), project.replace(project.getJarName())+"-working.jar");
        try{
            Files.copy(build.getShadedJAR().toPath(), workJar.toPath());
            JarLoader loader = new JarLoader(workJar, new ArrayList<>(), new LoaderConfig());
            loader.setManipulationEvent(new HandlerObfuscation());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
