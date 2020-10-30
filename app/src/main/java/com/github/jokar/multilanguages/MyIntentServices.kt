package com.github.jokar.multilanguages

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class MyIntentServices : IntentService("MyIntentServices") {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onHandleIntent(intent: Intent?) {}
    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext, getString(R.string.intent_service_create), Toast.LENGTH_SHORT).show()
    }

}