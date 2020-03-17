package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;

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
    private Logger mLogger;
    private boolean shouldOverwriteAttachMethod = true;
    private ClassWriter mClassWriter;

    public ActivityServiceClassVisitorV3(ClassWriter cv, Logger logger) {
        super(Opcodes.ASM5);
        this.cv = cv;
        mClassWriter = cv;
        this.mLogger = logger;
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
                shouldOverwriteAttachMethod = false;
            } else if (isAndroidxActivity() && name.equals("applyOverrideConfiguration")) {
                //是继承androidx.AppCompatActivity的activity,在 applyOverrideConfiguration
                //添加 overrideConfiguration.setTo(this.getBaseContext().getResources().getConfiguration());
                return new ApplyOverrideConfigurationMV(mClassWriter.visitMethod(access, name, descriptor,
                        signature, exceptions), this.className);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        replaceSuperMethod();
//        addAttchMethod();
    }

    /**
     * 替换super方法
     */
    private void replaceSuperMethod() {
        if (methods != null && !methods.isEmpty()) {
            for (MethodNode method : methods) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                        //替换内容
                        transformInvokeVirtual(method, (MethodInsnNode) abstractInsnNode);
                    }
                }
            }
        }
    }

    private void transformInvokeVirtual(MethodNode method, MethodInsnNode insnNode) {
        if (needAddAttach()
                && "attachBaseContext".equals(insnNode.name)
                && "(Landroid/content/Context;)V".equals(insnNode.desc)) {
            mLogger.error(insnNode.owner + " - " + insnNode.name + " - " + insnNode.desc);
//            method.instructions.insertBefore(insnNode, new LdcInsnNode(""));
//            method.instructions.insertBefore(insnNode, new LdcInsnNode(method.name));
            method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "com/github/jokar/multilanguages/library/MultiLanguage",
                    "setLocal",
                    "(Landroid/content/Context;)Landroid/content/Context;",
                    false));
            method.maxStack += 1;
        }
    }

    /**
     * 添加方法
     */
    private void addAttchMethod() {
        if (needAddAttach()) {
            if (shouldOverwriteAttachMethod) {
                //添加attachBaseContext方法
                mLogger.debug(String.format("add attach method to %s", name));
                if (isActivity()) {
                    MethodVisitorUtil.addActivityAttach(mClassWriter);
                } else if (isService()) {
                    MethodVisitorUtil.addServiceAttach(mClassWriter);
                } else if (isIntentService()) {
                    MethodVisitorUtil.addIntentServiceAttach(mClassWriter);
                }
            }

            if (needAddACMethod()) {
                //添加applyOverrideConfiguration方法
                mLogger.debug(String.format("add applyOverrideConfiguration method to %s", name));
                MethodVisitorUtil.addApplyOverrideConfiguration(mClassWriter, className);
            }
        }
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

    public boolean isShouldOverwriteAttachMethod() {
        return shouldOverwriteAttachMethod;
    }
}
