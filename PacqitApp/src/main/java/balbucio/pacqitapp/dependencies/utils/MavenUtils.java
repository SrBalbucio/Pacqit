package balbucio.pacqitapp.dependencies.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MavenUtils {

    public static List<File> findPomFiles(String m2Repository) {
        List<File> pomFiles = new ArrayList<>();
        File repositoryFolder = new File(m2Repository);

        if (!repositoryFolder.exists()) {
            System.out.println("A pasta .m2/repository não foi encontrada.");
            return pomFiles;
        }

        findPomFilesInDirectory(repositoryFolder, pomFiles);
        return pomFiles;
    }

    private static void findPomFilesInDirectory(File directory, List<File> pomFiles) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findPomFilesInDirectory(file, pomFiles);
                } else if (file.getName().endsWith(".pom")) {
                    pomFiles.add(file);
                }
            }
        }
    }
}
