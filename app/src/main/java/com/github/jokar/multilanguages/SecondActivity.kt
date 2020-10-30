package com.github.jokar.multilanguages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class SecondActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        val tvView = findViewById<TextView>(R.id.tv_1)
        tvView.text = getString(R.string.tv3_value)
    }

    companion object {
        fun enter(context: Context) {
            val intent = Intent(context, SecondActivity::class.java)
            context.startActivity(intent)
        }
    }
}