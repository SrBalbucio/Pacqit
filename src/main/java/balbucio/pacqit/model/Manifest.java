package balbucio.pacqit.model;

import balbucio.pacqit.PacqitConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.jar.Attributes;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manifest {

    private String version = "1.0";
    private String mainClass;
    private String jdkSpec = "20";

    public void save(File file) throws IOException {
        file.getParentFile().mkdirs();
        file.delete();
        java.util.jar.Manifest rm = new java.util.jar.Manifest();
        Attributes attributes = rm.getMainAttributes();

        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
        attributes.put(Attributes.Name.SPECIFICATION_VERSION, jdkSpec);
        attributes.put(Attributes.Name.IMPLEMENTATION_TITLE, "Pacqit Jar Builder");
        attributes.put(Attributes.Name.IMPLEMENTATION_VENDOR, "Pacqit");
        attributes.put(Attributes.Name.IMPLEMENTATION_VERSION, PacqitConst.VERSION);

        rm.write(new FileOutputStream(file));

    }

    public java.util.jar.Manifest getRealManifest(){
        java.util.jar.Manifest rm = new java.util.jar.Manifest();
        Attributes attributes = rm.getMainAttributes();

        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
        return rm;
    }

}
