package balbucio.pacqit.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageUtils {

    public static List<String> getPackagesInDirectory(File directory) {
        List<String> packages = new ArrayList<>();
        processDirectory(directory, "", packages);
        return packages;
    }

    private static void processDirectory(File directory, String currentPackage, List<String> packages) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                String packageName = currentPackage.isEmpty() ? file.getName() : currentPackage + "." + file.getName();
                packages.add(packageName);
                processDirectory(file, packageName, packages);
            }
        }
    }
}
