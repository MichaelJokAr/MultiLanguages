package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * applyOverrideConfiguration方法
 * Create by JokAr. on 2019-11-08.
 */
public class ApplyOverrideConfigurationMV extends MethodVisitor {
    private MethodVisitor mv;
    private String className;

    public ApplyOverrideConfigurationMV(MethodVisitor mv, String className) {
        super(Opcodes.ASM4, mv);
        this.mv = mv;
        this.className = className;
    }

    @Override
    public void visitCode() {
        MethodVisitorUtil.addSetTo(mv, className);
        super.visitCode();
    }
}
