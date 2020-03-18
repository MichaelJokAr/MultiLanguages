package com.github.jokar.multilanguages.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.github.jokar.multilanguages.PluginExtension
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.slf4j.LoggerFactory

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MultiLanguagesTransform extends Transform {
    private PluginExtension pluginExtension
    def static slf4jLogger = LoggerFactory.getLogger('logger')

    MultiLanguagesTransform(PluginExtension pluginExtension) {
        this.pluginExtension = pluginExtension
    }

    @Override
    String getName() {
        return 'MultiLanguages'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {
        super.transform(transformInvocation)
        //没有开启返回（默认开启）
        if (!pluginExtension.enable) {
            return
        }
        println("+-----------------------------------------------------------------------------+")
        println("|                     Multi Languages Plugin START                            |")
        println("+-----------------------------------------------------------------------------+")
        def startTime = System.currentTimeMillis()
        //
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        //删除之前的输出
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }

        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理DirectoryInput
                handleDirectoryInput(directoryInput, outputProvider)
            }
            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                //处理JarInput
                handleJarInput(jarInput, outputProvider)
            }
        }
        //
        def cost = (System.currentTimeMillis() - startTime) / 1000
        println("+-----------------------------------------------------------------------------+")
        println("|                          Multi Languages Plugin END                         |")
        println("|                            Plugin cost ： $cost s                           |")
        println("+-----------------------------------------------------------------------------+")
    }
    /**
     * 处理DirectoryInput
     * @param directoryInput
     * @param outputProvider
     */
    void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (checkClassFile(name)) {
                    def classReader = new ClassReader(file.bytes)
                    def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def classNode = new ActivityServiceClassVisitor(classWriter, slf4jLogger)
                    classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
                    classNode.accept(classWriter)
                    //
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }

        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }


    /**
     * 处理JarInput
     * @param jarInput
     * @param outputProvider
     */
    void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }

            def jarFile = new JarFile(jarInput.file)
            def enumeration = jarFile.entries()
            def tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            //
            def jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                def jarEntry = (JarEntry) enumeration.nextElement()
                def entryName = jarEntry.name
                def zipEntry = new ZipEntry(entryName)
                def inputStream = jarFile.getInputStream(jarEntry)
                //插桩class
                if (checkClassFile(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    def classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    def classNode = new ActivityServiceClassVisitor(classWriter, slf4jLogger)
                    classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
                    classNode.accept(classWriter)
                    //
                    def code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }

                jarOutputStream.closeEntry()
            }

            //结束
            jarOutputStream.close()
            jarFile.close()
            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    /**
     * 检查文件是否为需要处理的问题
     * @param name
     * @return
     */
    boolean checkClassFile(String name) {
        //只处理需要的class文件
        return (name.endsWith(".class")
                && !name.startsWith("R\$")
                && "R.class" != name
                && "BuildConfig.class" != name
                && !name.startsWith("android/support")
                && !name.startsWith("androidx/core/app"))
    }
}