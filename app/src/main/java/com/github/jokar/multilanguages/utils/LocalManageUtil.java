package com.github.jokar.multilanguages.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.github.jokar.multilanguages.R;
import com.github.jokar.multilanguages.library.MultiLanguage;

import java.util.Locale;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    public static String getSelectLanguage(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return context.getString(R.string.language_auto);
            case 1:
                return context.getString(R.string.language_cn);
            case 2:
                return context.getString(R.string.language_traditional);
            case 3:
            default:
                return context.getString(R.string.language_en);
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {

        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.CHINA;
            case 2:
                return Locale.TAIWAN;
            case 3:
            default:
                return Locale.ENGLISH;
        }
    }


    public static void saveSystemCurrentLanguage(Context context) {
        SPUtil.getInstance(context).setSystemCurrentLocal(MultiLanguage.getSystemLocal(context));
    }

    /**
     * 保存系统语言
     * @param context
     * @param newConfig
     */
    public static void saveSystemCurrentLanguage(Context context, Configuration newConfig) {

        SPUtil.getInstance(context).setSystemCurrentLocal(MultiLanguage.getSystemLocal(newConfig));
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        MultiLanguage.setApplicationLanguage(context);
    }
}
