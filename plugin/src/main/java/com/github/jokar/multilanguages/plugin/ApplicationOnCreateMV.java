package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ApplicationOnCreateMV extends MethodVisitor {

    private MethodVisitor mv;
    private String className;

    public ApplicationOnCreateMV(MethodVisitor mv, String className) {
        super(Opcodes.ASM5, mv);
        this.mv = mv;
        this.className = className;
    }

    @Override
    public void visitCode() {
        MethodVisitorUtil.addOnCreate(mv);
        super.visitCode();
    }
}
