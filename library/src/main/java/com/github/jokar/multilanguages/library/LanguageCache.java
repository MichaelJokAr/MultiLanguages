package com.github.jokar.multilanguages.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageCache {

    private static final String SP_REPO_NAME = "multi_language_setting";
    private static final String KEY_APP_LANGUAGE = "multi_language_app_setting";
    private static SharedPreferences sSharedPreferences;
    private static Locale sSystemCurrentLocale = Locale.ENGLISH;

    private LanguageCache() {
    }

    static void init(Context context) {
        sSharedPreferences = context.getSharedPreferences(SP_REPO_NAME, Context.MODE_PRIVATE);
    }

    static void saveAppLanguage(Context context, MultiLanguage.AppLanguage language) {
        SharedPreferences.Editor edit = sSharedPreferences.edit();
        edit.putString(KEY_APP_LANGUAGE, language.name());
        edit.apply();
        MultiLanguage.setLocale(context);
        MultiLanguage.setApplicationLocale(context);
    }

    static MultiLanguage.AppLanguage getAppLanguage() {
        String languageName = sSharedPreferences.getString(KEY_APP_LANGUAGE, MultiLanguage.AppLanguage.FOLLOW_SYSTEM.name());
        return MultiLanguage.AppLanguage.valueOf(languageName);
    }

    /**
     * 获取选择的语言设置
     *
     * @return
     */
    static Locale getAppLanguageLocale() {
        switch (getAppLanguage()) {
            case FOLLOW_SYSTEM:
                return getSystemCurrentLocale();
            case CHINA:
                return Locale.CHINA;
            case TAIWAN:
                return Locale.TAIWAN;
            case ENGLISH:
            default:
                return Locale.ENGLISH;
        }
    }

    static Locale getSystemCurrentLocale() {
        return sSystemCurrentLocale;
    }

    static void saveSystemCurrentLanguage() {
        sSystemCurrentLocale = MultiLanguage.getSystemLocale();
    }

    /**
     * 通过改变后的Configuration保存当前系统语言
     *
     * @param newConfig
     */
    static void saveSystemCurrentLanguage(Configuration newConfig) {
        sSystemCurrentLocale = MultiLanguage.getSystemLocale(newConfig);
    }
}
