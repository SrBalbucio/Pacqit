package balbucio.pacqit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manifest {

    private String version = "1.0";
    private String mainClass;
    private String createdBy = "Pacqit 1.0";
    private String jdkSpec = "20";

    public void save(File file){
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Manifest-Version: "+version+"\n");
            writer.write("Created-By: " + createdBy + "\n");
            writer.write("Build-Jdk-Spec: " + jdkSpec + "\n");
            writer.write("Main-Class: " + mainClass + "\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
