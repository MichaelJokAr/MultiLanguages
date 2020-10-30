package com.github.jokar.multilanguages.utils

import android.content.Context
import android.content.res.Configuration
import com.github.jokar.multilanguages.R
import com.github.jokar.multilanguages.library.MultiLanguage
import java.util.*

object LocalManageUtil {
    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    fun getSystemLocale(context: Context): Locale {
        return SPUtil.getInstance(context)?.systemCurrentLocal!!
    }

    fun getSelectLanguage(context: Context): String {
        return when (SPUtil.getInstance(context)?.selectLanguage) {
            0 -> context.getString(R.string.language_auto)
            1 -> context.getString(R.string.language_cn)
            2 -> context.getString(R.string.language_traditional)
            3 -> context.getString(R.string.language_en)
            else -> context.getString(R.string.language_en)
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context context
     * @return Local
     */
    fun getSetLanguageLocale(context: Context): Locale {
        return when (SPUtil.getInstance(context)?.selectLanguage) {
            0 -> getSystemLocale(context)
            1 -> Locale.CHINA
            2 -> Locale.TAIWAN
            3 -> Locale.ENGLISH
            else -> Locale.ENGLISH
        }
    }

    fun saveSystemCurrentLanguage(context: Context) {
        SPUtil.getInstance(context)?.systemCurrentLocal = MultiLanguage.getSystemLocal(context)
    }

    /**
     * 保存系统语言
     *
     * @param context   context
     * @param newConfig newConfig
     */
    fun saveSystemCurrentLanguage(context: Context, newConfig: Configuration?) {
        SPUtil.getInstance(context)?.systemCurrentLocal = MultiLanguage.getSystemLocal(newConfig)
    }

    fun saveSelectLanguage(context: Context, select: Int) {
        SPUtil.getInstance(context)?.saveLanguage(select)
        MultiLanguage.setApplicationLanguage(context)
    }
}