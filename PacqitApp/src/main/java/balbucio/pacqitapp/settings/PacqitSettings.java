package balbucio.pacqitapp.settings;

import balbucio.pacqitapp.utils.ThemeUtils;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private String themeName = "Arc Dark - Orange";


    public void save(){
        File config = new File(System.getenv("APPDATA")+"/Pacqit", "settings.yml");
        try {
            config.getParentFile().mkdirs();
            config.createNewFile();
            PrintWriter writer = new PrintWriter(config);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yml = new Yaml(new Constructor(PacqitSettings.class, new LoaderOptions()), new Representer(options));
            yml.dump(this, writer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setThemeInApp(boolean warn) {
        try{
            UIManager.setLookAndFeel(ThemeUtils.getWithName(themeName));
            if(warn) {
                JOptionPane.showMessageDialog(null, "Some previously opened screens should not switch themes for now. Restart Pacqit to see the changes.");
            }
            save();
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "There was an unexpected error loading the Pacqit theme, so the default theme will be used.\n" +
                    "\n" +
                    "Try changing the theme from the project menu (using the pacqit command in the console) or using the pacqitapp command)");
            try {UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());} catch (UnsupportedLookAndFeelException ignored) { ignored.printStackTrace();}
        }
    }
}
