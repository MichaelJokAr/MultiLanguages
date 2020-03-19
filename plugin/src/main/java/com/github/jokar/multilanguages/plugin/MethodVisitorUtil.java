package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * Create by JokAr. on 2019-07-09.
 */
public class MethodVisitorUtil {

    /**
     * 添加activity下的attachBaseContext
     *
     * @param cw
     */
    public static void addActivityAttach(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "attachBaseContext",
                "(Landroid/content/Context;)V",
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

    /**
     * 添加intentService类下的attachBaseContext
     *
     * @param cw
     */
    public static void addIntentServiceAttach(ClassWriter cw) {
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
        mv.visitLocalVariable("newBase", "Landroid/content/Context;",
                null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * 添加service下的attachBaseContext
     *
     * @param cw
     */
    public static void addServiceAttach(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "attachBaseContext",
                "(Landroid/content/Context;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/github/jokar/multilanguages/library/MultiLanguage",
                "setLocal", "(Landroid/content/Context;)Landroid/content/Context;",
                false);
        mv.visitMethodInsn(INVOKESPECIAL, "android/app/Service", "attachBaseContext",
                "(Landroid/content/Context;)V", false);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("base", "Landroid/content/Context;",
                null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * 添加applyOverrideConfiguration方法
     *
     * @param cw
     * @param className
     */
    public static void addApplyOverrideConfiguration(ClassWriter cw, String className) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "applyOverrideConfiguration",
                "(Landroid/content/res/Configuration;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "getBaseContext",
                "()Landroid/content/Context;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/Context", "getResources",
                "()Landroid/content/res/Resources;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/res/Resources", "getConfiguration",
                "()Landroid/content/res/Configuration;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/res/Configuration", "setTo",
                "(Landroid/content/res/Configuration;)V", false);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity",
                "applyOverrideConfiguration", "(Landroid/content/res/Configuration;)V", false);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLocalVariable("this", "L" + className + ";", null, l0, l4, 0);
        mv.visitLocalVariable("overrideConfiguration", "Landroid/content/res/Configuration;",
                null, l0, l4, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * 添加 overrideConfiguration.setTo(this.getBaseContext().getResources().getConfiguration());
     */
    public static void addSetTo(MethodVisitor mv, String className) {
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, className,
                "getBaseContext", "()Landroid/content/Context;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/Context", "getResources",
                "()Landroid/content/res/Resources;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/res/Resources",
                "getConfiguration", "()Landroid/content/res/Configuration;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "android/content/res/Configuration",
                "setTo", "(Landroid/content/res/Configuration;)V", false);
        mv.visitLabel(l1);
    }

    /**
     * 是否是Activity类
     *
     * @return
     */
    public static boolean isActivity(String className) {
        if (className == null) {
            return false;
        }
        return ("android/support/v4/app/FragmentActivity".equals(className)
                || "android/support/v7/app/AppCompatActivity".equals(className)
                || "android/app/Activity".equals(className)
                || isAndroidxActivity(className));
    }

    /**
     * 是否是继承androidx.AppCompatActivity activity
     *
     * @return
     */
    public static boolean isAndroidxActivity(String className) {
        if (className == null) {
            return false;
        }
        return "androidx/appcompat/app/AppCompatActivity".equals(className);
    }


    public static boolean isService(String className) {
        if (className == null) {
            return false;
        }
        return "android/app/Service".equals(className);
    }

    public static boolean isIntentService(String className) {
        if (className == null) {
            return false;
        }
        return "android/app/IntentService".equals(className);
    }
}
