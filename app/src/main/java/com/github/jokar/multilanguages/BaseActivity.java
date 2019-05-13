package com.github.jokar.multilanguages;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.jokar.multilanguages.utils.LocalManageUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }
}
