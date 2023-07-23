package balbucio.pacqit.bytecode.event;

import balbucio.pacqit.utils.NameUtils;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.Arrays;

public interface JarManipulationEvent {

    void readyClass(ClassGen classGen);
    default MethodGen createMethodGen(Method m, ClassGen clazz, ConstantPoolGen c, Type[] parameters, Type returnType, String[] names){

        return new MethodGen(m.getAccessFlags(), returnType, parameters, names, m.getName(), clazz.getClassName(), new InstructionList(m.getCode().getCode()), c);
    }
    default FieldGen createFieldGen(Field f, ClassGen clazz, ConstantPoolGen cp){
        return new FieldGen(f, cp);
    }
    void modifyMethod(MethodGen gen);
    void modifyField(FieldGen gen);
}
