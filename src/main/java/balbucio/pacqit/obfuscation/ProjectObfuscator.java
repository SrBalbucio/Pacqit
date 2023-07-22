package balbucio.pacqit.obfuscation;

import balbucio.pacqit.ArgParse;
import balbucio.pacqit.Main;
import balbucio.pacqit.compiler.ProjectBuild;
import balbucio.pacqit.model.Project;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
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
    }
}
