package com.github.jokar.multilanguages

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class MyService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext, getString(R.string.service_create), Toast.LENGTH_SHORT).show()
    }
}