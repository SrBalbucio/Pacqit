package balbucio.pacqit.compiler;

import balbucio.pacqit.compiler.impl.JavaCCompiler;
import balbucio.pacqit.compiler.impl.OrzoCompiler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CompilerType {

    JAVAC("Java Compiler (javac)", "Official Java compiler built into your JDK.", JavaCCompiler.class),
    ORZO("Orzo", "Unofficial compiler made in Java (experimental).", OrzoCompiler.class);

    private final String name;
    private final String description;
    private final Class compiler;

}
