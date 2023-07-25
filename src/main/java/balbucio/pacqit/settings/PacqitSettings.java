package balbucio.pacqit.settings;

import balbucio.pacqit.model.ProjectImplementer;
import balbucio.pacqit.model.ProjectModule;
import balbucio.pacqit.utils.ThemeUtils;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileSystem;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.representer.Representer;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PacqitSettings {

    public static PacqitSettings getPacqitSettings(){
        PacqitSettings settings = new PacqitSettings();
        File config = new File(System.getenv("APPDATA")+"/Pacqit", "settings.yml");
        try{
            var loaderoptions = new LoaderOptions();
            TagInspector taginspector =
                    tag -> tag.getClassName().equals(PacqitSettings.class.getName());
            loaderoptions.setTagInspector(taginspector);
            Yaml yml = new Yaml(new Constructor(PacqitSettings.class, loaderoptions));
            settings = yml.load(new FileInputStream(config));
        }catch (Exception e){
            e.printStackTrace();
        }
        return settings;
    }

    private String themeName = "Monokai Pro";


    public void save(){
        File config = new File(System.getenv("APPDATA")+"/Pacqit", "settings.yml");
        try {
            PrintWriter writer = new PrintWriter(config);
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

    public void setThemeInApp() {
        try{
            Class<? extends IntelliJTheme.ThemeLaf> themeClazz = ThemeUtils.getWithName(themeName);
            UIManager.setLookAndFeel(themeClazz.newInstance());
        }catch (Exception e){
            e.printStackTrace();
            try {UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());} catch (UnsupportedLookAndFeelException ignored) {}
        }
    }
}
