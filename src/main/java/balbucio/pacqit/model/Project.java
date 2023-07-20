package balbucio.pacqit.model;

import balbucio.pacqit.ArgParse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Project {

    private String projectPackage = "org.example";
    private String name = "Unknown";
    private String version = "1.0-SNAPSHOT";
    private String sourcePath = "src/main/java";
    private String resourcePath = "src/main/resource";
    private String buildPluginsPath = "src/main/buildplugins";
    private String localLibrariesPath = "src/main/lib";
    private String outputPath = "build/release";
    private String compílePath = "target/classes";
    private String generatedPath = "target/generated";
    private boolean executableJar = false;
    private String mainClass = "org.example.Main";
    private String jarName = "${project.name}-${project.version}";
    private String JAVA_HOME = System.getProperty("java.home");
    private String javaVersion = "1.8";

    public String replace(String message){
        return message
                .replace("${project.name}", name)
                .replace("${project.version}", version)
                .replace("${project.projectPackage}", projectPackage)
                .replace("${project.javaVersion}", javaVersion);
    }

    public void save(File dir){
        File configFile = dir != null ? new File(dir, "project-config.yml") : new File("project-config.yml");
        try {
            PrintWriter writer = new PrintWriter(configFile);
            Yaml yml = new Yaml(new Constructor(Project.class, new LoaderOptions()));
            yml.dump(this, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Project loadProject(File dir){
        Project project = null;
        File configFile = dir != null ? new File(dir, "project-config.yml") : new File("project-config.yml");

        if(configFile.exists()){
            try {
                Yaml yml = new Yaml(new Constructor(Project.class, new LoaderOptions()));
                project = yml.load(new FileInputStream(configFile));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return project;
    }

    public static void createProjectTemplate(File dir){
        File configFile = dir != null ? new File(dir, "project-config.yml") : new File("project-config.yml");
        try {
            PrintWriter writer = new PrintWriter(configFile);
            Yaml yml = new Yaml(new Constructor(Project.class, new LoaderOptions()));
            yml.dump(new Project(), writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
