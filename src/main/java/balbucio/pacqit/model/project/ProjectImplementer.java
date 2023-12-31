package balbucio.pacqit.model.project;

import balbucio.pacqitapp.model.dependency.DependencyReceiver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectImplementer implements DependencyReceiver {

    private String implementerName = "Unknown";
    private String implementerVersion = "1.0-SNAPSHOT";
    private String implementerMainClass = "Main";
    private String implementerJarName = "${project.name}-${project.version}";
    private boolean implementerGenerateNativePackage = false;
    private String implementerToolNativePackage = "jpackage";
    private List<String> nativePackages = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    public String replace(String message){
        return message
                .replace("${project.name}", implementerName)
                .replace("${project.version}", implementerVersion);
    }
    public static List<ProjectImplementer> getImplementersInPath(File dir){
        List<ProjectImplementer> modules = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                if(hasProjectImplementerInPath(file)){
                    try {
                        File configFile = new File(file, "implementer-config.yml");
                        var loaderoptions = new LoaderOptions();
                        TagInspector taginspector =
                                tag -> tag.getClassName().equals(ProjectImplementer.class.getName());
                        loaderoptions.setTagInspector(taginspector);
                        Yaml yml = new Yaml(new Constructor(ProjectImplementer.class, loaderoptions));
                        modules.add(yml.load(new FileInputStream(configFile)));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return modules;
    }

    private static boolean hasProjectImplementerInPath(File dir){
        boolean has = false;
        if(dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (file.getName().equalsIgnoreCase("implementer-config.yml")) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    public void delete(File dir){
        File configFile = dir != null ? new File(dir, getImplementerName()+"/implementer-config.yml") : new File(getImplementerName()+"/implementer-config.yml");
        try {
            configFile.delete();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save(File dir){
        File configFile = dir != null ? new File(dir, getImplementerName()+"/implementer-config.yml") : new File(getImplementerName()+"/implementer-config.yml");
        try {
            PrintWriter writer = new PrintWriter(configFile);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yml = new Yaml(new Constructor(ProjectImplementer.class, new LoaderOptions()), new Representer(options));
            yml.dump(this, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addDependency(String dependecyParam) {

    }
}
