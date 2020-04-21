package com.github.jokar.multilanguages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.github.jokar.multilanguages.library.MultiLanguage;
import com.github.jokar.multilanguages.utils.LocalManageUtil;

public class SettingActivity extends BaseActivity {
    private TextView mUserSelect;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mUserSelect = findViewById(R.id.tv_user_select);
        mUserSelect.setText(getString(R.string.user_select_language,
                LocalManageUtil.getSelectLanguageName(this)));
        //
        setClick();
    }

    public static void enter(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    private void selectLanguage(MultiLanguage.AppLanguage appLanguage) {
        MultiLanguage.saveAppLanguage(this, appLanguage);
        MainActivity.reStart(this);
    }

    private void setClick() {
        //跟随系统
        findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(MultiLanguage.AppLanguage.FOLLOW_SYSTEM);
            }
        });
        //简体中文
        findViewById(R.id.btn_cn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(MultiLanguage.AppLanguage.CHINA);
            }
        });
        //繁体中文
        findViewById(R.id.btn_traditional).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(MultiLanguage.AppLanguage.TAIWAN);
            }
        });
        //english
        findViewById(R.id.btn_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(MultiLanguage.AppLanguage.ENGLISH);
            }
        });
    }
}
