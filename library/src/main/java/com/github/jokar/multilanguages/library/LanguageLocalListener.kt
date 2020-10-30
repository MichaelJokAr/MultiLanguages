package com.github.jokar.multilanguages.library

import android.content.Context
import java.util.*

/**
 * Create by JokAr. on 2019-07-08.
 */
interface LanguageLocalListener {
    /**
     * 获取选择设置语言
     *
     * @param context
     * @return
     */
    fun getSetLanguageLocale(context: Context?): Locale?
}