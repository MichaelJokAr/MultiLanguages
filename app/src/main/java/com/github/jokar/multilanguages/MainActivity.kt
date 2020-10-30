package com.github.jokar.multilanguages

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.jokar.multilanguages.MainActivity
import com.github.jokar.multilanguages.utils.LocalManageUtil.getSelectLanguage
import com.github.jokar.multilanguages.utils.LocalManageUtil.getSystemLocale

class MainActivity : BaseActivity() {
    private var startNewActivity: Button? = null
    private var startNewIntentService: Button? = null
    private var startSettingActivity: Button? = null
    private var startNewService: Button? = null

    //
    private var tvSystemLanguage: TextView? = null
    private var tvUserSelectLanguage: TextView? = null
    private var tvValue: TextView? = null
    private var tvValue2: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        //
        setValue()
    }

    private fun initView() {
        startNewActivity = findViewById(R.id.btn_1)
        startNewIntentService = findViewById(R.id.btn_2)
        startSettingActivity = findViewById(R.id.btn_3)
        startNewService = findViewById(R.id.btn_4)
        //
        tvSystemLanguage = findViewById(R.id.tv_system_language)
        tvUserSelectLanguage = findViewById(R.id.tv_user_select)
        tvValue = findViewById(R.id.tv_3)
        tvValue2 = findViewById(R.id.tv_4)
        //
        startNewActivity?.setOnClickListener { SecondActivity.enter(this@MainActivity) }
        //
        startSettingActivity?.setOnClickListener { SettingActivity.enter(this@MainActivity) }
        startNewIntentService?.setOnClickListener {
            val intent = Intent(this@MainActivity, MyIntentServices::class.java)
            startService(intent)
        }
        startNewService?.setOnClickListener {
            val intent = Intent(this@MainActivity, MyService::class.java)
            startService(intent)
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun setValue() {
        val string = getString(R.string.system_language,
                getSystemLocale(this).displayLanguage)
        tvSystemLanguage!!.text = string
        //
        tvUserSelectLanguage!!.text = getString(R.string.user_select_language,
                getSelectLanguage(this))
        //
        tvValue!!.text = getString(R.string.tv3_value)
        //
        tvValue2!!.text = applicationContext.getString(R.string.tv3_value)
    }

    companion object {
        @JvmStatic
        fun reStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}