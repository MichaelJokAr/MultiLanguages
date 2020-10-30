package com.github.jokar.multilanguages.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class SPUtil(context: Context) {
    private val TAG_LANGUAGE = "language_select"
    private val TAG_SYSTEM_LANGUAGE = "system_language"
    private val mSharedPreferences: SharedPreferences
    var systemCurrentLocal = Locale.ENGLISH
    fun saveLanguage(select: Int) {
        val edit = mSharedPreferences.edit()
        edit.putInt(TAG_LANGUAGE, select)
        edit.apply()
    }

    val selectLanguage: Int
        get() = mSharedPreferences.getInt(TAG_LANGUAGE, 0)

    companion object {
        private const val SP_NAME = "language_setting"

        @Volatile
        private var instance: SPUtil? = null
        fun getInstance(context: Context): SPUtil? {
            if (instance == null) {
                synchronized(SPUtil::class.java) {
                    if (instance == null) {
                        instance = SPUtil(context)
                    }
                }
            }
            return instance
        }
    }

    init {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }
}