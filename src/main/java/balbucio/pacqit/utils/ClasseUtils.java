package balbucio.pacqit.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClasseUtils {

    public static List<String> getClassesInDirectory(File directory) {
        List<String> classes = new ArrayList<>();
        processDirectory(directory, "", classes);
        return classes;
    }

    private static void processDirectory(File directory, String currentPackage, List<String> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                String packageName = currentPackage.isEmpty() ? file.getName() : currentPackage + "/" + file.getName();
                processDirectory(file, packageName, classes);
            } else if (file.isFile() && file.getName().endsWith(".java")) {
                String className = currentPackage.isEmpty() ? file.getName() : currentPackage + "/" + file.getName();
                classes.add(className);
            }
        }
    }
}
