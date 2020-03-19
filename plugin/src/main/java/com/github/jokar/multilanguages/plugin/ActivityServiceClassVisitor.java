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

import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isActivity;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isAndroidxActivity;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isIntentService;
import static com.github.jokar.multilanguages.plugin.MethodVisitorUtil.isService;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class ActivityServiceClassVisitor extends ClassNode implements Opcodes {
    private String superClassName;
    private String className;
    /**
     * 是否有applyOverrideConfiguration方法
     */
    private boolean hasACMethod;
    private Logger mLogger;
    private boolean shouldOverwriteAttachMethod = true;
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
            hasACMethod = "applyOverrideConfiguration".equals(name);
            if ("attachBaseContext".equals(name)) {
                shouldOverwriteAttachMethod = false;
            } else if (isAndroidxActivity(superClassName) && "applyOverrideConfiguration".equals(name)) {
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

        if (needAddAttach()) {
            replaceSuperMethod();

            addAttchMethod();
        }
    }

    /**
     * 替换super方法
     */
    private void replaceSuperMethod() {
        //判断是否重写了attachBaseContext 方法
        if (methods != null && !methods.isEmpty() && !shouldOverwriteAttachMethod) {
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

        if ((isActivity(insnNode.owner) || isService(insnNode.owner) || isIntentService(insnNode.owner))
                && "attachBaseContext".equals(insnNode.name)
                && "(Landroid/content/Context;)V".equals(insnNode.desc)) {
            mLogger.error("overwride class "+ insnNode.owner + " method: " + insnNode.name );
            method.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                    "com/github/jokar/multilanguages/library/MultiLanguage",
                    "setLocal",
                    "(Landroid/content/Context;)Landroid/content/Context;",
                    false));
        }
    }

    /**
     * 添加方法
     */
    private void addAttchMethod() {
        if (shouldOverwriteAttachMethod) {
            //添加attachBaseContext方法
            mLogger.error(String.format("add attach method to %s", name));
            if (isActivity(superClassName)) {
                MethodVisitorUtil.addActivityAttach(mClassWriter);
            } else if (isService(superClassName)) {
                MethodVisitorUtil.addServiceAttach(mClassWriter);
            } else if (isIntentService(superClassName)) {
                MethodVisitorUtil.addIntentServiceAttach(mClassWriter);
            }
        }

        if (needAddACMethod()) {
            //添加applyOverrideConfiguration方法
            mLogger.error(String.format("add applyOverrideConfiguration method to %s", name));
            MethodVisitorUtil.addApplyOverrideConfiguration(mClassWriter, className);
        }
    }

    /**
     * 是否需要添加
     *
     * @return
     */
    public boolean needAddAttach() {
        return isActivity(superClassName) || isService(superClassName) || isIntentService(superClassName);
    }

    /**
     * 是否需要添加applyOverrideConfiguration方法
     *
     * @return
     */
    public boolean needAddACMethod() {
        return isAndroidxActivity(superClassName) && !hasACMethod;
    }

}
