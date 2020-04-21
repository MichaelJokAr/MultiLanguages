package com.github.jokar.multilanguages.plugin;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.slf4j.Logger;

import java.util.ListIterator;

import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isActivity;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isAndroidxActivity;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isApplication;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isIntentService;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isService;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class ActivityServiceClassVisitor extends ClassNode implements Opcodes {
    private String superClassName;
    private String className;
    /**
     * 是否可以新增applyOverrideConfiguration方法
     */
    private boolean shouldOverwriteACMethod = true;
    /**
     * 是否可以新增onCreate方法
     */
    private boolean shouldOverwriteAppOnCreateMethod = true;
    /**
     * 是否可以新增attachBaseContext方法
     */
    private boolean shouldOverwriteAttachMethod = true;
    private Logger mLogger;
    private ClassWriter mClassWriter;

    public ActivityServiceClassVisitor(ClassWriter cv, Logger logger) {
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
            if ("attachBaseContext".equals(name)) {
                shouldOverwriteAttachMethod = false;
            } else if (isAndroidxActivity(superClassName) && "applyOverrideConfiguration".equals(name)) {
                shouldOverwriteACMethod = false;
                //是继承androidx.AppCompatActivity的activity,在 applyOverrideConfiguration
                //添加 overrideConfiguration.setTo(this.getBaseContext().getResources().getConfiguration());
                return new ApplyOverrideConfigurationMV(mClassWriter.visitMethod(access, name, descriptor,
                        signature, exceptions), this.className);
            } else if (isApplication(superClassName) && "onCreate".equals(name)) {
                shouldOverwriteAppOnCreateMethod = false;
                return new ApplicationOnCreateMV(mClassWriter.visitMethod(access, name, descriptor,
                        signature, exceptions), this.className);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }


    @Override
    public void visitEnd() {
        super.visitEnd();

        if (needAddAttach()) {
            if (shouldOverwriteAttachMethod) {
                // 添加attachBaseContext方法
                addAttachMethod();
            } else {
                // attachBaseContext方法复写涉及指令排序，使用tree api实现替换
                replaceAttachMethod();
            }
            if (needAddACMethod()) {
                // 添加applyOverrideConfiguration方法
                mLogger.error(String.format("add applyOverrideConfiguration method to %s", name));
                MethodVisitorUtil.addApplyOverrideConfiguration(mClassWriter, className);
            }
            if (needAddAppOnCreateMethod()) {
                // 添加onCreate方法
                mLogger.error(String.format("add onCreate method to %s", name));
                MethodVisitorUtil.addAppOnCreate(mClassWriter, className);
            }
        }
    }

    /**
     * 替换attachBaseContext方法
     */
    private void replaceAttachMethod() {
        if (methods != null && !methods.isEmpty()) {
            for (MethodNode method : methods) {
                if ("attachBaseContext".equals(method.name)) {
                    ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode abstractInsnNode = iterator.next();
                        if (abstractInsnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                            //替换内容
                            transformInvokeVirtual(method, (MethodInsnNode) abstractInsnNode);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public void transformInvokeVirtual(MethodNode method, MethodInsnNode insnNode) {
        if ("attachBaseContext".equals(insnNode.name)
                && "(Landroid/content/Context;)V".equals(insnNode.desc)) {
            if (isActivity(insnNode.owner) || isService(insnNode.owner) || isIntentService(insnNode.owner)) {
                mLogger.error("overwride class " + className+ " method: " + insnNode.name);
                method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/github/jokar/multilanguages/library/MultiLanguage",
                        "setLocale",
                        "(Landroid/content/Context;)Landroid/content/Context;",
                        false));
            } else if (isApplication(insnNode.owner)) {
                mLogger.error("overwride class " + className + " method: " + insnNode.name);
                method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/github/jokar/multilanguages/library/MultiLanguage",
                        "initCache",
                        "(Landroid/content/Context;)V",
                        false));
                method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/github/jokar/multilanguages/library/MultiLanguage",
                        "saveSystemCurrentLanguage",
                        "()V",
                        false));
                method.instructions.insertBefore(insnNode, new VarInsnNode(ALOAD, 0));
                method.instructions.insertBefore(insnNode, new VarInsnNode(ALOAD, 1));
                method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "com/github/jokar/multilanguages/library/MultiLanguage",
                        "setLocale",
                        "(Landroid/content/Context;)Landroid/content/Context;",
                        false));
            }
        }
    }

    /**
     * 添加attachBaseContext方法
     */
    private void addAttachMethod() {
        mLogger.error(String.format("add attach method to %s", name));
        if (isActivity(superClassName)) {
            MethodVisitorUtil.addActivityAttach(mClassWriter);
        } else if (isService(superClassName)) {
            MethodVisitorUtil.addServiceAttach(mClassWriter);
        } else if (isIntentService(superClassName)) {
            MethodVisitorUtil.addIntentServiceAttach(mClassWriter);
        } else if (isApplication(superClassName)) {
            MethodVisitorUtil.addApplicationAttach(mClassWriter, className);
        }
    }

    /**
     * 是否需要添加attachBaseContext
     *
     * @return
     */
    public boolean needAddAttach() {
        return isActivity(superClassName) ||
                isService(superClassName) ||
                isIntentService(superClassName) ||
                isApplication(superClassName);
    }

    /**
     * 是否需要添加applyOverrideConfiguration方法
     *
     * @return
     */
    public boolean needAddACMethod() {
        return isAndroidxActivity(superClassName) && shouldOverwriteACMethod;
    }

    public boolean needAddAppOnCreateMethod() {
        return isApplication(superClassName) && shouldOverwriteAppOnCreateMethod;
    }
}
