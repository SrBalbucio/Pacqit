package balbucio.pacqit.bytecode;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
public class BytecodeLogger {

    public static void logMethodInfo(MethodGen methodGen, File pathFile) {
        try {
            String methodName = methodGen.getName();
            if(methodName.equalsIgnoreCase("<init>")){
                methodName = "constructor-"+methodGen.getClassName();
            }
            File file = new File(pathFile, methodName + ".log");
            file.delete();

            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("Nome do método: " + methodGen.getName());
            writer.println("Descritor do método: " + methodGen.getSignature());
            writer.println("Máximo de variáveis locais: " + methodGen.getMaxLocals());
            writer.println("Máximo de elementos na pilha: " + methodGen.getMaxStack());

            // Imprimir as instruções
            writer.println("Instruções:");
            InstructionList instructionList = methodGen.getInstructionList();
            if (instructionList != null) {
                InstructionHandle[] instructionHandles = instructionList.getInstructionHandles();
                for (InstructionHandle ih : instructionHandles) {
                    writer.println(ih.toString());
                }
            }

            // Imprimir as informações das variáveis locais
            writer.println("Variáveis Locais:");
            LocalVariableGen[] localVariables = methodGen.getLocalVariables();
            if (localVariables != null) {
                for (LocalVariableGen localVariable : localVariables) {
                    writer.println("  Nome: " + localVariable.getName());
                    writer.println("  Tipo: " + localVariable.getType());
                    writer.println("  Índice: " + localVariable.getIndex());
                    writer.println();
                }
            }
            writer.flush();
            writer.close();
            System.out.println("Log do método '" + methodName + "' criado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logFieldInfo(FieldGen fieldGen, File pathFile) {
        try {
            String fieldName = fieldGen.getName();
            File file = new File(pathFile, fieldName + ".log");
            file.delete();
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("Nome do campo: " + fieldGen.getName());
            writer.println("Tipo do campo: " + fieldGen.getType());
            writer.println("Modificadores do campo: " + fieldGen.getAccessFlags());

            // Se o campo tiver um valor padrão definido
            if (fieldGen.getInitValue() != null) {
                writer.println("Valor inicial: " + fieldGen.getInitValue());
            }

            writer.println();
            writer.flush();
            writer.close();

            System.out.println("Log do campo '" + fieldName + "' criado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logClassInfo(ClassGen classGen, File pathFile) {
        try {
            String className = classGen.getClassName();
            File file = new File(pathFile, className + ".log");
            file.delete();
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println("Nome da classe: " + className);

            // Imprimir os nomes dos métodos
            writer.println("Métodos:");
            for (Method method : classGen.getMethods()) {
                writer.println("  " + method.getName());
            }

            // Imprimir os nomes dos campos
            writer.println("Campos:");
            for (Field field : classGen.getFields()) {
                writer.println("  " + field.getName());
            }

            writer.println();
            writer.flush();
            writer.close();

            System.out.println("Log da classe '" + className + "' criado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
