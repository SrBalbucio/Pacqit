package balbucio.pacqit.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public class JarUtils {

    public static List<String> getJars(File folder) {
        List<String> jars = new ArrayList<>();
        for (File j : folder.listFiles()) {
            if (j.isFile() && FilenameUtils.isExtension(j.getName(), "jar")) {
                jars.add(j.getAbsolutePath());
            }
        }
        return jars;
    }

    public static List<File> getJarFiles(File folder) {
        List<File> jars = new ArrayList<>();
        for (File j : folder.listFiles()) {
            if (j.isFile() && FilenameUtils.isExtension(j.getName(), "jar")) {
                jars.add(j);
            }
        }
        return jars;
    }

    public static File createJar(File jarFile, File clazzDirectory) throws Exception {
        if (!jarFile.exists()) {
            jarFile.createNewFile();
        }
        List<String> classFiles = getClassFilesInDirectory(clazzDirectory);

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile));
        for (String classFilePath : classFiles) {
            File classFile = new File(clazzDirectory, classFilePath);
            addClassToJar(classFile, classFilePath, jarOutputStream);
        }
        return jarFile;
    }

    public static List<String> getClassFilesInDirectory(File directory) {
        List<String> classFiles = new ArrayList<>();
        processDirectory(directory, classFiles);
        return classFiles;
    }

    private static void processDirectory(File directory, List<String> classFiles) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, classFiles);
            } else if (file.isFile() && file.getName().endsWith(".class")) {
                String classFilePath = file.getAbsolutePath().replace(directory.getAbsolutePath(), "");
                if (classFilePath.startsWith(File.separator)) {
                    classFilePath = classFilePath.substring(1);
                }

                classFilePath = classFilePath.replace(File.separator, ".");

                classFiles.add(classFilePath);
            }
        }
    }

    private static void addClassToJar(File classFile, String classFilePath, JarOutputStream jarOutputStream) throws Exception {
        JarEntry jarEntry = new JarEntry(classFilePath);
        jarOutputStream.putNextEntry(jarEntry);

        byte[] buffer = new byte[1024];
        int bytesRead;
        FileInputStream stream = new FileInputStream(classFile);
        while ((bytesRead = stream.read(buffer)) != -1) {
            jarOutputStream.write(buffer, 0, bytesRead);
        }

        jarOutputStream.closeEntry();
    }

    public static void combineJars(File jar1Path, File jar2Path, File outputJarPath, File tempDir) throws Exception {
        // Criar diretório temporário para desempacotar os JARs originais
        tempDir.mkdirs();

        // Desempacotar o primeiro JAR para o diretório temporário
        unpackJar(jar1Path, tempDir);
        // Desempacotar o segundo JAR para o diretório temporário
        unpackJar(jar2Path, tempDir);

        // Criar novo JAR com os arquivos do diretório temporário
        packJar(tempDir, outputJarPath);

        // Excluir o diretório temporário
        deleteDirectory(tempDir);
    }

    public static void unpackJar(File jarPath, File targetDir) throws Exception {
        try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarPath))) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (!entry.isDirectory()) {
                    File file = new File(targetDir, entry.getName());
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }

    public static void packJar(File directory, File jarPath) throws Exception {
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarPath))) {
            pack(directory, directory, jarOutputStream);
        }
    }

    private static void pack(File rootDir, File currentDir, JarOutputStream jarOutputStream) throws Exception {
        File[] files = currentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    pack(rootDir, file, jarOutputStream);
                } else {
                    String relativePath = rootDir.toURI().relativize(file.toURI()).getPath();
                    JarEntry jarEntry = new JarEntry(relativePath);
                    jarOutputStream.putNextEntry(jarEntry);

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            jarOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }

    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
