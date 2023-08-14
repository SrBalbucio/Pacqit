package balbucio.pacqitapp.utils;

import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemeUtils {

    private static String FLAT_PACKAGE = "com.formdev.flatlaf.intellijthemes";

    public static List<String> getThemeNames(){
        List<String> themeNames = new ArrayList<>();
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : FlatAllIJThemes.INFOS) {
            themeNames.add(info.getName());
        }
        return themeNames;
    }

    public static LookAndFeel getWithName(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String classname = Arrays.stream(FlatAllIJThemes.INFOS)
                .filter(f -> f.getName().equalsIgnoreCase(name)).findFirst()
                .orElse(new FlatAllIJThemes.FlatIJLookAndFeelInfo("Arc Dark - Orange", "com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme", true))
                .getClassName();
        return (LookAndFeel) Class.forName(classname).newInstance();
    }

}
