package balbucio.pacqit.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClasseUtils {

    public static List<String> getClassNames(File directory){
        List<String> names = new ArrayList<>();
        getClassesInDirectory(directory).forEach(c -> {
            names.add(extractClassName(directory.getAbsolutePath()+"/"+c));
        });
        return names;
    }

    public static String extractClassName(String javaFilePath) {
        File file = new File(javaFilePath);
        StringBuilder classText = new StringBuilder();

        try (FileReader fileReader = new FileReader(file)) {
            int character;
            while ((character = fileReader.read()) != -1) {
                classText.append((char) character);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String classContent = classText.toString();
        String className = null;

        // Procurar a ocorrência da palavra-chave 'class' no arquivo
        int classKeywordIndex = classContent.indexOf("class");
        if (classKeywordIndex != -1) {
            // Encontrou a palavra-chave 'class', agora procura o nome da classe logo após
            int classNameStart = classKeywordIndex + "class".length();
            int classNameEnd = classContent.indexOf("{", classNameStart);
            if (classNameEnd != -1) {
                className = classContent.substring(classNameStart, classNameEnd).trim();
            }
        }

        return className;
    }

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
