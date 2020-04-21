package com.github.jokar.multilanguages.utils;

import android.content.Context;

import com.github.jokar.multilanguages.R;
import com.github.jokar.multilanguages.library.LanguageCache;
import com.github.jokar.multilanguages.library.MultiLanguage;

public class LocalManageUtil {

    public static String getSelectLanguageName(Context context) {
        MultiLanguage.AppLanguage language = MultiLanguage.getAppLanguage();
        if (language == MultiLanguage.AppLanguage.FOLLOW_SYSTEM) {
            return context.getString(R.string.language_auto);
        } else if (language == MultiLanguage.AppLanguage.CHINA) {
            return context.getString(R.string.language_cn);
        } else if (language == MultiLanguage.AppLanguage.TAIWAN) {
            return context.getString(R.string.language_traditional);
        } else {
            return context.getString(R.string.language_en);
        }
    }
}
