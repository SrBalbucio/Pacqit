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

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectModule {

    private String modulePath;
    private String moduleName;
    private String moduleVersion;
    private String moduleJarName;
    private List<String> modules;
    private List<String> dependencies;

    public String replace(String message){
        return message
                .replace("${project.name}", moduleName)
                .replace("${project.version}", moduleVersion);
    }

    public static List<ProjectModule> getModulesInPath(File dir){
        List<ProjectModule> modules = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                if(hasProjectModuleInPath(file)){
                    try {
                        File configFile = new File(file, "module-config.yml");
                        var loaderoptions = new LoaderOptions();
                        TagInspector taginspector =
                                tag -> tag.getClassName().equals(Project.class.getName());
                        loaderoptions.setTagInspector(taginspector);
                        Yaml yml = new Yaml(new Constructor(ProjectModule.class, loaderoptions));
                        modules.add(yml.load(new FileInputStream(configFile)));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return modules;
    }

    private static boolean hasProjectModuleInPath(File dir){
        boolean has = false;
        if(dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (file.getName().equalsIgnoreCase("module-config.yml")) {
                    has = true;
                }
            }
        }
        return has;
    }

}
