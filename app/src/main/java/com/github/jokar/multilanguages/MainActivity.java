package com.github.jokar.multilanguages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.jokar.multilanguages.library.MultiLanguage;
import com.github.jokar.multilanguages.utils.LocalManageUtil;

public class MainActivity extends BaseActivity {

    private Button startNewActivity;
    private Button startNewIntentService;
    private Button startSettingActivity;
    private Button startNewService;
    //
    private TextView tvSystemLanguage;
    private TextView tvUserSelectLanguage;
    private TextView tvValue;
    private TextView tvValue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //
        setValue();
    }

    private void initView() {
        startNewActivity = findViewById(R.id.btn_1);
        startNewIntentService = findViewById(R.id.btn_2);
        startSettingActivity = findViewById(R.id.btn_3);
        startNewService = findViewById(R.id.btn_4);
        //
        tvSystemLanguage = findViewById(R.id.tv_system_language);
        tvUserSelectLanguage = findViewById(R.id.tv_user_select);
        tvValue = findViewById(R.id.tv_3);
        tvValue2 = findViewById(R.id.tv_4);
        //
        startNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondActivity.enter(MainActivity.this);
            }
        });
        //
        startSettingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.enter(MainActivity.this);
            }
        });


        startNewIntentService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyIntentServices.class);
                startService(intent);
            }
        });

        startNewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);
            }
        });
    }


    @SuppressLint("StringFormatInvalid")
    private void setValue() {
        String string = getString(R.string.system_language,
                MultiLanguage.getSystemCurrentLocale().getDisplayLanguage());
        tvSystemLanguage.setText(string);
        //
        tvUserSelectLanguage.setText(getString(R.string.user_select_language,
                LocalManageUtil.getSelectLanguageName(this)));
        //
        tvValue.setText(getString(R.string.tv3_value));
        //
        tvValue2.setText(getApplicationContext().getString(R.string.tv3_value));
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
