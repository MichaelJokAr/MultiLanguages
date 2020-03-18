package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class AttachBaseContextMethod extends MethodNode {
    private String superClassName;

    public AttachBaseContextMethod(MethodVisitor methodVisitor,
                                   String superClassName,
                                   int access,
                                   String name,
                                   String descriptor,
                                   String signature,
                                   String[] exceptions) {
        super(Opcodes.ASM5, access, name, descriptor, signature, exceptions);
        this.mv = methodVisitor;
        this.superClassName = superClassName;
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);

    }

}
