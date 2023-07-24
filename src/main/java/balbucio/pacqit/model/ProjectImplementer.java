package balbucio.pacqit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectImplementer {

    private String implementerPath = "";
    private String implementerName;
    private String implementerPackage;
    private String implementerVersion;
    private String implementerMainClass;
    private String implementerJarName;
    private boolean implementerGenerateNativePackage;
    private String implementerToolNativePackage;
    private List<String> nativePackages = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    public String getImplementerPath() {
        return implementerPath+"/"+implementerName+"/";
    }

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
                                tag -> tag.getClassName().equals(Project.class.getName());
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
}
