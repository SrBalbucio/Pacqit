package balbucio.pacqit.model;

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

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectModule {

    private String modulePath = "";
    private String moduleName = "Unknown";
    private String moduleVersion = "1.0-SNAPSHOT";
    private List<String> modules = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();

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
                                tag -> tag.getClassName().equals(ProjectModule.class.getName());
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
                if (file.getName().contains("module-config")) {
                    has = true;
                }
            }
        }
        return has;
    }

    public void delete(File dir){
        File configFile = dir != null ? new File(dir, getModulePath()+"/"+getModuleName()+"/module-config.yml") : new File(getModulePath()+"/"+getModuleName()+"/module-config.yml");
        try {
            configFile.delete();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save(File dir){
        File configFile = dir != null ? new File(dir, getModulePath()+"/"+getModuleName()+"/module-config.yml") : new File(getModulePath()+"/"+getModuleName()+"/module-config.yml");
        try {
            PrintWriter writer = new PrintWriter(configFile);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yml = new Yaml(new Constructor(ProjectModule.class, new LoaderOptions()), new Representer(options));
            yml.dump(this, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
