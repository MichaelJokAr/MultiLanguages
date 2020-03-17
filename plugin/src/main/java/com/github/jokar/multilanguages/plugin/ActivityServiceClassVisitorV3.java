package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.ListIterator;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class ActivityServiceClassVisitorV3 extends ClassNode implements Opcodes {
    private String superClassName;
    private String className;
    /**
     * 是否有applyOverrideConfiguration方法
     */
    private boolean hasACMethod;

    public ActivityServiceClassVisitorV3(ClassWriter cv) {
        super(Opcodes.ASM5);
        this.cv = cv;
        if(methods != null && !methods.isEmpty()){
            for (MethodNode method : methods) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()){
                    AbstractInsnNode abstractInsnNode = iterator.next();
                   if(abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL){
                       //替换内容
                       transformInvokeVirtual( method, (MethodInsnNode) abstractInsnNode);
                   }
                }
            }
        }
    }

    private void transformInvokeVirtual(MethodNode method, MethodInsnNode insnNode) {
        if("".equals(insnNode.owner)){

        }
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
            hasACMethod = name.equals("applyOverrideConfiguration");
            if (name.equals("attachBaseContext")) {

            } else if (isAndroidxActivity() && name.equals("applyOverrideConfiguration")) {
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
        return (superClassName.equals("android/support/v4/app/FragmentActivity")
                || superClassName.equals("android/support/v7/app/AppCompatActivity")
                || superClassName.equals("android/app/Activity")
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
        return superClassName.equals("androidx/appcompat/app/AppCompatActivity");
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
        return superClassName.equals("android/app/Service")
                && !isAndroidxPackageName(); //排除androidx包里的
    }

    public boolean isIntentService() {
        if (className == null || superClassName == null) {
            return false;
        }
        return superClassName.equals("android/app/IntentService")
                && !isAndroidxPackageName(); //排除androidx包里的
    }

    public String getClassName() {
        return className;
    }


}
