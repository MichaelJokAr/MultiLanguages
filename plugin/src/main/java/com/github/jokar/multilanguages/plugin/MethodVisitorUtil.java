package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * Create by JokAr. on 2019-07-09.
 */
public class MethodVisitorUtil {

    public static void addActivityAttach(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "attachBaseContext", "(Landroid/content/Context;)V",
                null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/github/jokar/multilanguages/library/MultiLanguage",
                "setLocal", "(Landroid/content/Context;)Landroid/content/Context;",
                false);
        mv.visitMethodInsn(INVOKESPECIAL, "android/app/Activity", "attachBaseContext",
                "(Landroid/content/Context;)V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("newBase", "Landroid/content/Context;",
                null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }


    public static void addServiceAttach(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "attachBaseContext",
                "(Landroid/content/Context;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/github/jokar/multilanguages/library/MultiLanguage",
                "setLocal", "(Landroid/content/Context;)Landroid/content/Context;", false);
        mv.visitMethodInsn(INVOKESPECIAL, "android/app/IntentService", "attachBaseContext",
                "(Landroid/content/Context;)V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("newBase", "Landroid/content/Context;", null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }
}
