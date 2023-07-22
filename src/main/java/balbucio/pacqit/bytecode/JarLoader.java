package balbucio.pacqit.bytecode;

import balbucio.pacqit.bytecode.event.JarLoadEvent;
import balbucio.pacqit.bytecode.event.JarManipulationEvent;
import balbucio.pacqit.utils.SimpleEntry;
import de.milchreis.uibooster.components.ProgressDialog;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {

    private List<String> classes;
    private ClassPathRepository repository;
    private JarLoadEvent loadEvent;
    private JarManipulationEvent manipulationEvent;
    private LoaderConfig config;

    public JarLoader(File jarFile, List<String> classes, LoaderConfig config) {
        this.config = config;
        this.classes = classes;
        createGUI();
        ClassPath classPath = new ClassPath(jarFile.getAbsolutePath());
        this.repository = new ClassPathRepository(classPath);
        startLoad();
    }

    private ProgressDialog dialog;

    public void createGUI() {
        if (config.GUI) {
            this.dialog = config.app.getUiBooster().showProgressDialog("Obfuscating your code...", "Obfuscating", 0, 100);
        }
    }

    public void progress(int value) {
        if (config.GUI) {
            dialog.setProgress(value);
        }
    }

    public void closeGUI(){
        if(config.GUI){
            dialog.close();
        }
    }

    public void setLoadEvent(JarLoadEvent loadEvent) {
        this.loadEvent = loadEvent;
    }

    public void setManipulationEvent(JarManipulationEvent manipulationEvent) {
        this.manipulationEvent = manipulationEvent;
    }

    private List<SimpleEntry<JavaClass, ClassGen, ConstantPoolGen>> allClassGen = new ArrayList<>();
    private List<SimpleEntry<Method, MethodGen, ClassGen>> allMethodsGen = new ArrayList<>();
    private List<SimpleEntry<Field, FieldGen, ClassGen>> allFieldGen = new ArrayList<>();

    public void startLoad() {
        progress(0);
        classes.forEach(cname -> classProducer(repository.findClass(cname)));
        progress(10);
        allClassGen.forEach(e -> {
            getMethodsInJavaClass(e.getKey()).forEach(m -> methodProducer(m, e.getValue(), e.getValue2()));
            getFieldsInJavaClass(e.getKey()).forEach(f -> fieldProducer(f, e.getValue(), e.getValue2()));
        });
        progress(20);

        allMethodsGen.forEach(e -> manipulationEvent.modifyMethod(e.getValue()));
        progress(40);
        allFieldGen.forEach(e -> manipulationEvent.modifyField(e.getValue()));
        progress(60);
        checkAndSaveAll();
        progress(100);
    }

    public void classProducer(JavaClass clazz) {
        if (clazz != null) {
            ClassGen classGen = new ClassGen(clazz);
            manipulationEvent.readyClass(classGen);
            ConstantPoolGen cpGen = classGen.getConstantPool();
            allClassGen.add(new SimpleEntry<>(clazz, classGen, cpGen));
            BytecodeLogger.logClassInfo(classGen, config.LOG_PATH);
        }
    }

    public void methodProducer(Method m, ClassGen clazzGen, ConstantPoolGen cp) {
        Type[] p = m.getArgumentTypes();
        for (int i = 0; i < p.length; i++) {
            Type t = p[i];
            if (hasClassGen(t.getClassName())) {
                p[i] = new ObjectType(getClassGenByName(t.getClassName()).getClassName());
            }
        }
        Type returnType = m.getReturnType();
        if (hasClassGen(returnType.getClassName())) {
            returnType = new ObjectType(getClassGenByName(returnType.getClassName()).getClassName());
        }
        MethodGen mgen = manipulationEvent.createMethodGen(m, clazzGen, cp, p, returnType);
        allMethodsGen.add(new SimpleEntry<>(m, mgen, clazzGen));
        BytecodeLogger.logMethodInfo(mgen, config.LOG_PATH);
        clazzGen.removeMethod(m);
    }


    public void fieldProducer(Field f, ClassGen clazzGen, ConstantPoolGen cp) {
        FieldGen fgen = manipulationEvent.createFieldGen(f, clazzGen, cp);
        if (hasClassGen(f.getType().getClassName())) {
            fgen.setType(new ObjectType(getClassGenByName(f.getType().getClassName()).getClassName()));
        }
        allFieldGen.add(new SimpleEntry<>(f, fgen, clazzGen));
        BytecodeLogger.logFieldInfo(fgen, config.LOG_PATH);
        clazzGen.removeField(f);
    }

    public void checkAndSaveAll() {
        allMethodsGen.forEach(e -> {
            ClassGen gen = e.getValue2();
            gen.addMethod(e.getValue().getMethod());
        });
        allFieldGen.forEach(e -> {
            ClassGen gen = e.getValue2();
            gen.addField(e.getValue().getField());
        });

        allClassGen.forEach(e -> {
            ClassGen classgen = e.getValue();
            ConstantPoolGen cp = e.getValue2();
            getMethodsInJavaClass(classgen).forEach(method -> {
                InstructionList itls = new InstructionList(method.getCode().getCode());
                for (InstructionHandle itl : itls) {
                    if (itl.getInstruction() instanceof InvokeInstruction ivk) {
                        String methodName = ivk.getMethodName(cp);
                        if (hasMethodGen(methodName)) {
                            SimpleEntry<Method, MethodGen, ClassGen> em = getMethodEntry(methodName);
                            InvokeInstruction newivk =
                                    new INVOKEVIRTUAL(cp.addMethodref(em.getValue2().getClassName(),
                                            em.getValue().getName(),
                                            ivk.getSignature(cp)));
                            itls.redirectBranches(itl, itls.append(newivk));
                        }
                    }
                }
            });
            try {
                classgen.getJavaClass().dump(e.getKey().getFileName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean hasClassGen(String className) {
        return allClassGen.stream().anyMatch(e -> e.getKey().getClassName().equals(className));
    }

    public ClassGen getClassGenByName(String className) {
        return allClassGen.stream().filter(e -> e.getKey().getClassName().equals(className)).findFirst().orElse(null).getValue();
    }

    public boolean hasMethodGen(String method) {
        return allMethodsGen.stream().anyMatch(e -> e.getKey().equals(method));
    }

    public MethodGen getMethodGen(String method) {
        return allMethodsGen.stream().filter(e -> e.getKey().getName().equals(method)).findFirst().orElse(null).getValue();
    }

    public SimpleEntry<Method, MethodGen, ClassGen> getMethodEntry(String method) {
        return allMethodsGen.stream().filter(e -> e.getKey().getName().equals(method)).findFirst().orElse(null);
    }

    public JavaClass getJavaClass(String className) {
        return repository.findClass(className);
    }

    public Method getMethodInJavaClass(JavaClass clazz, String methodName) {
        return Arrays.stream(clazz.getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().orElse(null);
    }

    public List<Method> getMethodsInJavaClass(JavaClass clazz) {
        return List.of(clazz.getMethods());
    }

    public List<Field> getFieldsInJavaClass(JavaClass clazz) {
        return List.of(clazz.getFields());
    }

    public List<Method> getMethodsInJavaClass(ClassGen clazz) {
        return List.of(clazz.getMethods());
    }

    public List<Field> getFieldsInJavaClass(ClassGen clazz) {
        return List.of(clazz.getFields());
    }
}