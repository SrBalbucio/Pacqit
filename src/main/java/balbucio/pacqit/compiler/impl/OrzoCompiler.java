package balbucio.pacqit.compiler.impl;

import balbucio.pacqit.compiler.Compiler;

import java.io.File;

public class OrzoCompiler implements Compiler {
    @Override
    public boolean compile(File source, File localLibraries, File compilePath, File javaHome, String javaVersion) {
        return false;
    }
}
