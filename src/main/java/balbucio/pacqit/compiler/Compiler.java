package balbucio.pacqit.compiler;

import java.io.File;

public interface Compiler {
    boolean compile(File source, File localLibraries, File compilePath, File javaHome, String javaVersion);
    boolean buildJAR();
}
