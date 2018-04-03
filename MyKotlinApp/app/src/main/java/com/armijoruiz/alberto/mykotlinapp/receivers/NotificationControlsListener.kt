package com.armijoruiz.alberto.mykotlinapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService

class NotificationControlsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("NotControlsListener", "sending intent to service")
        val action = intent.action
        val intent:Intent = Intent(context,PlayMusicService::class.java)
        intent.action = action
        context.startService(intent)
    }
}