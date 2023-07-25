package balbucio.pacqit.utils;

import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ThemeUtils {

    public static List<String> getThemeNames(){
        List<String> themeNames = new ArrayList<>();
            findIntelliJThemeLafs().forEach(c -> {
                try {
                    themeNames.add(((String)c.getDeclaredField("NAME").get(null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        return themeNames;
    }

    public static Class<? extends IntelliJTheme.ThemeLaf> getWithName(String name){
        return (Class<? extends IntelliJTheme.ThemeLaf>) findIntelliJThemeLafs().stream().filter(c -> {
            try {
                return ((String)c.getDeclaredField("NAME").get(null)).equalsIgnoreCase(name);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }).findFirst().orElse(FlatMonocaiIJTheme.class);
    }

    public static List<Class<? extends IntelliJTheme.ThemeLaf>> findIntelliJThemeLafs() {
        List<Class<? extends IntelliJTheme.ThemeLaf>> intelliJThemeLafs = new ArrayList<>();
        String packageName = "com.formdev.flatlaf.intellijthemes";
        String packagePath = packageName.replace('.', '/');

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    String[] files = directory.list();

                    if (files != null) {
                        for (String file : files) {
                            if (file.endsWith(".class")) {
                                String className = packageName + "." + file.substring(0, file.length() - 6);
                                Class<?> clazz = Class.forName(className);

                                if (IntelliJTheme.ThemeLaf.class.isAssignableFrom(clazz)) {
                                    intelliJThemeLafs.add((Class<? extends IntelliJTheme.ThemeLaf>) clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ignored) {}

        return intelliJThemeLafs;
    }
}
