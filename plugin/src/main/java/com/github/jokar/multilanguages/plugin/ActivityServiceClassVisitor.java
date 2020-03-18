package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class ActivityServiceClassVisitor extends ClassVisitor implements Opcodes {
    private String superClassName;
    private String className;
    /**
     * 是否有applyOverrideConfiguration方法
     */
    private boolean hasACMethod;
    private boolean shouldOverwriteAttachMethod = true;
    private List<String> overwriteClass;

    public ActivityServiceClassVisitor(ClassWriter cv, List<String> overwriteClass) {
        super(Opcodes.ASM5, cv);
        this.overwriteClass = overwriteClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        superClassName = superName;
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {

        if (needAddAttach()) {
            hasACMethod = "applyOverrideConfiguration".equals(name);
            if ("attachBaseContext".equals(name)) {
                if (showOverwriteAttachMethod()) {
                    shouldOverwriteAttachMethod = true;
                    //删除原有 attachBaseContext 方法
                    return null;
                } else {
                    shouldOverwriteAttachMethod = false;
                }
            } else if (isAndroidxActivity() && "applyOverrideConfiguration".equals(name)) {
                //是继承androidx.AppCompatActivity的activity,在 applyOverrideConfiguration
                //添加 overrideConfiguration.setTo(this.getBaseContext().getResources().getConfiguration());
                return new ApplyOverrideConfigurationMV(cv.visitMethod(access, name, descriptor,
                        signature, exceptions), this.className);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    /**
     * 是否需要添加
     *
     * @return
     */
    public boolean needAddAttach() {
        return isActivity() || isService() || isIntentService();
    }

    /**
     * 是否需要添加applyOverrideConfiguration方法
     *
     * @return
     */
    public boolean needAddACMethod() {
        return isAndroidxActivity() && !hasACMethod;
    }

    /**
     * 是否是Activity类
     *
     * @return
     */
    public boolean isActivity() {
        if (className == null || superClassName == null) {
            return false;
        }
        return ("android/support/v4/app/FragmentActivity".equals(superClassName)
                || "android/support/v7/app/AppCompatActivity".equals(superClassName)
                || "android/app/Activity".equals(superClassName)
                || isAndroidxActivity())
                && !isAndroidxPackageName(); //排除androidx包里的
    }

    /**
     * 是否是继承androidx.AppCompatActivity activity
     *
     * @return
     */
    private boolean isAndroidxActivity() {
        if (superClassName == null) {
            return false;
        }
        return "androidx/appcompat/app/AppCompatActivity".equals(superClassName);
    }

    /**
     * 是否是androidx包名下类
     *
     * @return
     */
    public boolean isAndroidxPackageName() {
        return className.contains("androidx/core/app");
    }

    public boolean isService() {
        if (className == null || superClassName == null) {
            return false;
        }
        return "android/app/Service".equals(superClassName)
                && !isAndroidxPackageName(); //排除androidx包里的
    }

    public boolean isIntentService() {
        if (className == null || superClassName == null) {
            return false;
        }
        return "android/app/IntentService".equals(superClassName)
                && !isAndroidxPackageName(); //排除androidx包里的
    }

    public String getClassName() {
        return className;
    }

    /**
     * 是否强制覆盖重写attach方法
     *
     * @return
     */
    private boolean showOverwriteAttachMethod() {
        for (String clazz : overwriteClass) {
            String clazzName = clazz.replaceAll("\\.","\\/");
            if (clazzName.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean isShouldOverwriteAttachMethod() {
        return shouldOverwriteAttachMethod;
    }
}
