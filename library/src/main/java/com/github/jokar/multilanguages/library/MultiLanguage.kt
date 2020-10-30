package com.github.jokar.multilanguages.library

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

/**
 * Create by JokAr. on 2019-07-08.
 */
object MultiLanguage {
    private var languageLocalListener: LanguageLocalListener? = null
    fun init(listener: LanguageLocalListener?) {
        languageLocalListener = listener
    }

    fun setLocal(context: Context): Context {
        return updateResources(context, getSetLanguageLocale(context))
    }

    /**
     * 设置语言类型
     */
    fun setApplicationLanguage(context: Context) {
        val resources = context.applicationContext.resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        val locale = getSetLanguageLocale(context)
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context.applicationContext.createConfigurationContext(config)
            Locale.setDefault(locale)
        }
        resources.updateConfiguration(config, dm)
    }

    /**
     * @param context
     */
    fun onConfigurationChanged(context: Context) {
        setLocal(context)
        setApplicationLanguage(context)
    }

    /**
     * 获取选择的语言
     *
     * @param context
     * @return
     */
    private fun getSetLanguageLocale(context: Context): Locale? {
        return if (languageLocalListener != null) {
            languageLocalListener!!.getSetLanguageLocale(context)
        } else Locale.ENGLISH
    }

    /**
     * 更新语言设置
     *
     * @param context
     * @param locale
     * @return
     */
    private fun updateResources(context: Context, locale: Locale?): Context {
        var context = context
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    /**
     * 获取系统语言
     * @param newConfig
     * @return
     */
    fun getSystemLocal(newConfig: Configuration): Locale {
        val locale: Locale
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newConfig.locales[0]
        } else {
            newConfig.locale
        }
        return locale
    }

    /**
     * 获取系统语言
     * @param context
     * @return
     */
    fun getSystemLocal(context: Context?): Locale {
        val locale: Locale
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        return locale
    }
}