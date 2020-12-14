package com.github.jokar.multilanguages.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
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
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException,
            InterruptedException, IOException {
        super.transform(transformInvocation)
        println("${pluginExtension.toString()}")

        //没有开启返回（默认开启）
        if (!pluginExtension.enable) {
            // 将输入原封不动的复制到输出
            disablePlugin(transformInvocation)
            return
        }

        println("+-----------------------------------------------------------------------------+")
        println("|                     Multi Languages Plugin START                            |")
        println("+-----------------------------------------------------------------------------+")
        def startTime = System.currentTimeMillis()
        //
        def isIncremental = transformInvocation.isIncremental()
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        println("+------------------------" + "isIncremental:" + isIncremental + "----------------------------------+")
        //删除之前的输出
        if (outputProvider != null && !isIncremental) {
            outputProvider.deleteAll()
        }

        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理DirectoryInput
                handleDirectoryInput(directoryInput, outputProvider, isIncremental)
            }
            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                //处理JarInput
                handleJarInput(jarInput, outputProvider, isIncremental)
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
     * 不开启插件
     * @param transformInvocation
     */
    private void disablePlugin(TransformInvocation transformInvocation) {
        println("+-----------------------------------------------------------------------------+")
        println("|                     Multi Languages Plugin DISABLED                         |")
        println("+-----------------------------------------------------------------------------+")

        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider

        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //处理DirectoryInput
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                //处理JarInput
                def destJar = outputProvider.getContentLocation(jarInput.name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, destJar)
            }
        }
    }

    void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        if (isIncremental) {
            File dest = outputProvider.getContentLocation(directoryInput.getName(),
                    directoryInput.getContentTypes(), directoryInput.getScopes(),
                    Format.DIRECTORY)
            FileUtils.forceMkdir(dest)
            String srcDirPath = directoryInput.getFile().getAbsolutePath()
            String destDirPath = dest.getAbsolutePath()
            Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()
            for (Map.Entry<File, Status> changedFile : fileStatusMap.entrySet()) {
                Status status = changedFile.getValue()
                File inputFile = changedFile.getKey()
                String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath);
                File destFile = new File(destFilePath)
                switch (status) {
                    case Status.NOTCHANGED:
                        break
                    case Status.REMOVED:
                        if (destFile.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            destFile.delete()
                        }
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        try {
                            FileUtils.touch(destFile)
                        } catch (IOException ignored) {
                        }
                        def file = inputFile
                        def name = file.name
                        if (checkClassFile(name)) {
                            def classReader = new ClassReader(file.bytes)
                            def classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            def classNode = new ActivityServiceClassVisitor(classWriter, slf4jLogger)
                            classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
                            classNode.accept(classWriter)
                            //
                            byte[] code = classWriter.toByteArray()
                            FileOutputStream fos = new FileOutputStream(destFile)
                            fos.write(code)
                            fos.close()
                        } else {
                            if (inputFile.isFile()) {
                                FileUtils.touch(destFile)
                                FileUtils.copyFile(inputFile, destFile)
                            }
                        }
                        break
                }
            }
        } else {
            handleDirectoryInput(directoryInput, outputProvider)
        }
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

    void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        if (isIncremental) {
            Status status = jarInput.getStatus()
            File dest = outputProvider.getContentLocation(
                    jarInput.getFile().getAbsolutePath(),
                    jarInput.getContentTypes(),
                    jarInput.getScopes(),
                    Format.JAR)
            switch (status) {
                case Status.NOTCHANGED:
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    handleJarInput(jarInput, outputProvider)
                    break
                case Status.REMOVED:
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    break
            }
        } else {
            handleJarInput(jarInput, outputProvider)
        }
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
                && !name.startsWith("androidx/"))
    }
}