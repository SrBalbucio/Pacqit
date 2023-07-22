package balbucio.pacqit.obfuscation.impl;

import balbucio.pacqit.bytecode.event.JarManipulationEvent;
import balbucio.pacqit.utils.NameUtils;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;

public class HandlerObfuscation implements JarManipulationEvent {
    @Override
    public void readyClass(ClassGen classGen) {

    }

    @Override
    public void modifyMethod(MethodGen gen) {
        gen.setName(NameUtils.generateJavaValidName());
    }

    @Override
    public void modifyField(FieldGen gen) {
        gen.setName(NameUtils.generateJavaValidName());
    }
}
