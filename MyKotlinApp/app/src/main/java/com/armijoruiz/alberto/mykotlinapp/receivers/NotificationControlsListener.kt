package com.armijoruiz.alberto.mykotlinapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.armijoruiz.alberto.mykotlinapp.other.NEXT
import com.armijoruiz.alberto.mykotlinapp.other.PLAYPAUSE
import com.armijoruiz.alberto.mykotlinapp.other.PREV
import com.armijoruiz.alberto.mykotlinapp.services.PlayMusicService

class NotificationControlsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("NotControlsListener", "sending intent to service")
        val action = intent.action
        val intent:Intent = Intent(context,PlayMusicService::class.java)
        intent.setAction(action)
        if(action == null){
            Log.i("NotControlsListener","NULL ACTION")
        }
        if(context != null){
            Log.i("NotControlsListener", "sending action: "+action)
            when(action){
                PLAYPAUSE,NEXT, PREV -> context.startService(intent)
            }
        }else{
            Log.i("NotControlsListener", "bad context")
        }


    }
}