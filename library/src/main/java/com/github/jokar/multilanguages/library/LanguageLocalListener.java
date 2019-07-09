package com.github.jokar.multilanguages.library;

import android.content.Context;

import java.util.Locale;

/**
 * Create by JokAr. on 2019-07-08.
 */
public interface LanguageLocalListener {

    /**
     * 获取选择设置语言
     *
     * @param context
     * @return
     */
    Locale getSetLanguageLocale(Context context);
}
