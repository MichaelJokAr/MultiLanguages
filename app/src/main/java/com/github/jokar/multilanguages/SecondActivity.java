package com.github.jokar.multilanguages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class SecondActivity extends BaseActivity {

    public static void enter(Context context) {
        Intent intent = new Intent(context, SecondActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        TextView tvView = findViewById(R.id.tv_1);
        tvView.setText(getString(R.string.tv3_value));
    }
}
