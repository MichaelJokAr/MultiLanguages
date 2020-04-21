package com.github.jokar.multilanguages.library;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Create by JokAr. on 2019-07-08.
 */
public class MultiLanguage {

    private MultiLanguage() {
    }

    public static void initCache(Context context) {
        LanguageCache.init(context);
    }

    public static void init(final Context context) {
        context.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                MultiLanguage.saveSystemCurrentLanguage(newConfig);
                MultiLanguage.setApplicationLocale(context);
            }

            @Override
            public void onLowMemory() {
            }
        });
    }

    public static void saveSystemCurrentLanguage() {
        LanguageCache.saveSystemCurrentLanguage();
    }

    public static void saveSystemCurrentLanguage(Configuration newConfig) {
        LanguageCache.saveSystemCurrentLanguage(newConfig);
    }

    public static Locale getSystemCurrentLocale() {
        return LanguageCache.getSystemCurrentLocale();
    }

    public static void saveAppLanguage(Context context, MultiLanguage.AppLanguage language) {
        LanguageCache.saveAppLanguage(context, language);
    }

    public static MultiLanguage.AppLanguage getAppLanguage() {
        return LanguageCache.getAppLanguage();
    }

    /**
     * 根据当前配置，生成新的context
     *
     * @param context attach base context
     * @return 配置后的context
     */
    public static Context setLocale(Context context) {
        Locale locale = LanguageCache.getAppLanguageLocale();
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7.0 后支持设置多个偏好语言
                LocaleList.setDefault(new LocaleList(locale));
            }
            config.setLocale(locale); // Android 4.2 后使用 setLocale 设置语言
            context = context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, dm);
        }
        return context;
    }

    /**
     * 设置application context locale
     *
     * @param context
     */
    public static void setApplicationLocale(Context context) {
        Locale locale = LanguageCache.getAppLanguageLocale();
        Locale.setDefault(locale);
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Android 7.0 后支持设置多个偏好语言
                LocaleList.setDefault(new LocaleList(locale));
            }
            config.setLocale(locale); // Android 4.2 后使用 setLocale 设置语言
        }
        resources.updateConfiguration(config, dm);
    }

    /**
     * 从configuration中获取系统语言
     *
     * @param newConfig
     * @return
     */
    public static Locale getSystemLocale(Configuration newConfig) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = newConfig.getLocales().get(0);
        } else {
            locale = newConfig.locale;
        }
        return locale;
    }

    /**
     * 从locale default中获取系统语言
     *
     * @return
     */
    public static Locale getSystemLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public enum AppLanguage {
        FOLLOW_SYSTEM,
        CHINA,
        TAIWAN,
        ENGLISH
    }
}
