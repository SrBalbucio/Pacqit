package balbucio.pacqit.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JarUtils {

    public static List<String> getJars(File folder){
        List<String> jars = new ArrayList<>();
        for(File j : folder.listFiles()){
            if(j.isFile() && FilenameUtils.isExtension(j.getName(), "jar")){
                jars.add(j.getAbsolutePath());
            }
        }
        return jars;
    }
}
