package balbucio.pacqit.bytecode;

import balbucio.pacqit.bytecode.event.JarLoadEvent;
import balbucio.pacqit.bytecode.event.JarManipulationEvent;
import balbucio.pacqit.logger.BuildLoggerFormat;
import balbucio.pacqit.logger.LoaderLoggerFormat;
import balbucio.pacqit.utils.SimpleEntry;
import de.milchreis.uibooster.components.ProgressDialog;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class JarLoader {

    private Logger LOADER_LOGGER;
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
        configureLogger();
    }

    private void configureLogger(){
        LOADER_LOGGER = Logger.getLogger("LOADER");
        LOADER_LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        LoaderLoggerFormat format = new LoaderLoggerFormat();
        handler.setFormatter(format);
        LOADER_LOGGER.addHandler(handler);
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
        classes.forEach(cname -> {
            try {
                classProducer(repository.loadClass(cname.replace(".java", "").replace("/", ".")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
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
        closeGUI();
    }

    public void classProducer(JavaClass clazz) {
        if (clazz != null) {
            ClassGen classGen = new ClassGen(clazz);
            LOADER_LOGGER.info("Class "+clazz.getClassName()+" loaded successfully.");
            manipulationEvent.readyClass(classGen);
            LOADER_LOGGER.info("Modifiers applied to the class "+clazz.getClassName());
            ConstantPoolGen cpGen = classGen.getConstantPool();
            allClassGen.add(new SimpleEntry<>(clazz, classGen, cpGen));
            BytecodeLogger.logClassInfo(classGen, config.LOG_PATH);
            int classNameIndex = classGen.getClassNameIndex();
            ConstantClass constant = (ConstantClass) cpGen.getConstant(classNameIndex);
            cpGen.setConstant(classNameIndex, new ConstantClass(constant));
            classGen.setConstantPool(cpGen);
            LOADER_LOGGER.info("Stored "+clazz.getClassName()+" class. New class name: "+classGen.getClassName());
        }
    }

    public void methodProducer(Method m, ClassGen clazzGen, ConstantPoolGen cp) {
        MethodGen tempMethod = new MethodGen(m, clazzGen.getClassName(), cp);
        LocalVariableGen[] variables = tempMethod.getLocalVariables();
        String[] names = new String[m.getArgumentTypes().length];
        Type[] p = m.getArgumentTypes();
        for (int i = 0; i < p.length; i++) {
            Type t = p[i];
            if (hasClassGen(t.getClassName())) {
                p[i] = new ObjectType(getClassGenByName(t.getClassName()).getClassName());
            }
            LocalVariableGen localVariableGen = getVariableByType(t, variables);
            if(localVariableGen != null){
                names[i] = localVariableGen.getName();
            }
        }

        Type returnType = m.getReturnType();
        if (hasClassGen(returnType.getClassName())) {
            returnType = new ObjectType(getClassGenByName(returnType.getClassName()).getClassName());
        }
        tempMethod = null;
        MethodGen mgen = manipulationEvent.createMethodGen(m, clazzGen, cp, p, returnType, names);
        allMethodsGen.add(new SimpleEntry<>(m, mgen, clazzGen));
        BytecodeLogger.logMethodInfo(mgen, config.LOG_PATH);
        clazzGen.removeMethod(m);
        LOADER_LOGGER.info("Original method "+m.getName()+" removed from its parent class. New parameters applied to the method.");
    }


    public void fieldProducer(Field f, ClassGen clazzGen, ConstantPoolGen cp) {
        FieldGen fgen = manipulationEvent.createFieldGen(f, clazzGen, cp);
        if (hasClassGen(f.getType().getClassName())) {
            fgen.setType(new ObjectType(getClassGenByName(f.getType().getClassName()).getClassName()));
        }
        allFieldGen.add(new SimpleEntry<>(f, fgen, clazzGen));
        BytecodeLogger.logFieldInfo(fgen, config.LOG_PATH);
        clazzGen.removeField(f);
        LOADER_LOGGER.info("Field "+f.getName()+" removed from its parent class. Field type replaced by the correct one.");
    }

    public void checkAndSaveAll() {
        allMethodsGen.forEach(e -> {
            MethodGen mgen = e.getValue();
            ClassGen gen = e.getValue2();
            mgen.setMaxStack();
            mgen.setMaxLocals();
            gen.addMethod(mgen.getMethod());
        });
        allFieldGen.forEach(e -> {
            ClassGen gen = e.getValue2();
            gen.addField(e.getValue().getField());
        });

        allClassGen.forEach(e -> {
            ClassGen classgen = e.getValue();
            ConstantPoolGen cp = e.getValue2();
            getMethodsInJavaClass(classgen).forEach(method -> {
                MethodGen mm = new MethodGen(method, classgen.getClassName(), cp);
                InstructionList itls = mm.getInstructionList();
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
                            LOADER_LOGGER.info("The method "+method.getName()+" had called this other method "+em.getValue().getName()+", it has been changed and corrected by the new one;");
                        }
                    }
                }
                mm.setInstructionList(itls);
                classgen.replaceMethod(method, mm.getMethod());
            });

            try {
                File folder = new File(config.OUT_PATH, extractPackage(classgen).replace(".", "/"));
                folder.mkdirs();
                System.out.println(extractClassName(classgen));
                classgen.getJavaClass().dump(new File(folder, extractClassName(classgen)+".class"));
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
        return allMethodsGen.stream().anyMatch(e -> e.getKey().getName().equals(method));
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

    public String extractPackage(ClassGen classgen){
        String className = classgen.getClassName();

        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return className.substring(0, lastDotIndex);
        } else {
            return "";
        }
    }

    public String extractClassName(ClassGen classgen){
        return classgen.getClassName().replace(extractPackage(classgen), "");
    }

    public LocalVariableGen getVariableByType(Type type, LocalVariableGen[] variables){
        for (LocalVariableGen variable : variables) {
            if(variable.getType().getClassName().equals(type.getClassName())){
                return variable;
            }
        }
        return null;
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
