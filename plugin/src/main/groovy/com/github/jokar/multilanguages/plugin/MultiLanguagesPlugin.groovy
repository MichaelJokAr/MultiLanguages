package com.github.jokar.multilanguages.plugin

import com.android.build.gradle.AppExtension
import com.github.jokar.multilanguages.PluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 多语言插件
 */
class MultiLanguagesPlugin implements Plugin<Project> {
    public static final String PLUGIN_NAME = "multiLanguages"

    @Override
    void apply(Project project) {
        //注册plugin参数插件
        project.extensions.create(PLUGIN_NAME, PluginExtension)
        def ext = project.extensions.findByName(PLUGIN_NAME) as PluginExtension
        //注册插桩插件
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new MultiLanguagesTransform(ext))
    }

}