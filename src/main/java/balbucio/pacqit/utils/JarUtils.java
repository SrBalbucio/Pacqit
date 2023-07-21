package balbucio.pacqit.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

    public static void directoryToJar(File jarFile, File... directory) throws Exception {

        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile));
        for(File dir : directory) {
            if(dir != null) {
                for (File file : dir.listFiles()) {
                    addFilesToJar(file, "", jarOutputStream);
                }
            }
        }
        jarOutputStream.close();
    }

    private static void addFilesToJar(File source, String parentPath, JarOutputStream jarOutputStream) throws Exception {
        byte[] buffer = new byte[1024];
        String entryName = parentPath + source.getName();

        if (source.isDirectory()) {
            if (!entryName.isEmpty()) {
                if (!entryName.endsWith("/")) {
                    entryName += "/";
                }
                JarEntry jarEntry = new JarEntry(entryName);
                jarOutputStream.putNextEntry(jarEntry);
                jarOutputStream.closeEntry();
            }
            for (File file : source.listFiles()) {
                addFilesToJar(file, entryName, jarOutputStream);
            }
        } else {
            JarEntry jarEntry = new JarEntry(entryName);
            jarOutputStream.putNextEntry(jarEntry);

            FileInputStream inputStream = new FileInputStream(source);
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                jarOutputStream.write(buffer, 0, length);
            }

            inputStream.close();
            jarOutputStream.closeEntry();
        }
    }

    public static void extractJar(File jarFilePath, File destDir) throws Exception {

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (JarFile jarFile = new JarFile(jarFilePath)) {
            for (JarEntry entry : jarFile.stream().toArray(JarEntry[]::new)) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    File outFile = new File(destDir, entryName);
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    try (InputStream inputStream = jarFile.getInputStream(entry);
                         FileOutputStream outputStream = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }
}
