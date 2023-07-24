package balbucio.pacqit.compiler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CompilerType {

    JAVAC("Java Compiler (javac)", "Official Java compiler built into your JDK."),
    ORZO("Orzo", "Unofficial compiler made in Java (experimental).");

    private final String name;
    private final String description;

}
