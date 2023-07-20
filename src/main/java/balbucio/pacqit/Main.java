package balbucio.pacqit;

import balbucio.pacqit.model.Project;
import balbucio.pacqit.model.ProjectBuild;

import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        ArgParse parse = new ArgParse(args);
        new Main(parse);
    }

    private ArgParse parse;
    private Project project;
    private ProjectBuild projectBuild;

    public Main(ArgParse parse){
        this.parse = parse;
        this.project = Project.loadProject(parse.getProjectDir());
        this.projectBuild = new ProjectBuild(project, parse);
        projectBuild.createPath();
    }
}