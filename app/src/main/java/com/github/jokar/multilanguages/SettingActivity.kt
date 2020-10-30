package com.github.jokar.multilanguages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.github.jokar.multilanguages.MainActivity.Companion.reStart
import com.github.jokar.multilanguages.utils.LocalManageUtil.getSelectLanguage
import com.github.jokar.multilanguages.utils.LocalManageUtil.saveSelectLanguage

class SettingActivity : BaseActivity() {
    private var mUserSelect: TextView? = null

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mUserSelect = findViewById(R.id.tv_user_select)
        mUserSelect?.text = getString(R.string.user_select_language, getSelectLanguage(this))
        //
        setClick()
    }

    private fun selectLanguage(select: Int) {
        saveSelectLanguage(this, select)
        reStart(this)
    }

    private fun setClick() {
        //跟随系统
        findViewById<View>(R.id.btn_auto).setOnClickListener { selectLanguage(0) }
        //简体中文
        findViewById<View>(R.id.btn_cn).setOnClickListener { selectLanguage(1) }
        //繁体中文
        findViewById<View>(R.id.btn_traditional).setOnClickListener { selectLanguage(2) }
        //english
        findViewById<View>(R.id.btn_en).setOnClickListener { selectLanguage(3) }
    }

    companion object {
        fun enter(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }
    }
}