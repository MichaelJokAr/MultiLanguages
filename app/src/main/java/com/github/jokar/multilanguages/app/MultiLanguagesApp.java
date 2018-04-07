package com.github.jokar.multilanguages.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.github.jokar.multilanguages.utils.LocalManageUtil;

public class MultiLanguagesApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalManageUtil.setApplicationLanguage(this);
    }
}
