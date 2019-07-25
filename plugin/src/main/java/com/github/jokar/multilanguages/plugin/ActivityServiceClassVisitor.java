package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class ActivityServiceClassVisitor extends ClassVisitor implements Opcodes {
    private String superClassName;

    public ActivityServiceClassVisitor(ClassWriter cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        superClassName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                     String[] exceptions) {
        //删除原有 attachBaseContext 方法
        if (needAddAttach() && name.equals("attachBaseContext")) {
            return null;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public boolean needAddAttach() {
        return isActivity() || isService();
    }

    /**
     * 是否是Activity类
     *
     * @return true是 activity类
     */
    public boolean isActivity() {
        return superClassName.equals("android/support/v4/app/FragmentActivity")
                || superClassName.equals("android/support/v7/app/AppCompatActivity")
                || superClassName.equals("android/app/Activity");
    }

    /**
     * 是否是service类
     *
     * @return true 是 service类
     */
    public boolean isService() {
        return superClassName.equals("android/app/IntentService")
                || superClassName.equals("android/app/Service");
    }
}
