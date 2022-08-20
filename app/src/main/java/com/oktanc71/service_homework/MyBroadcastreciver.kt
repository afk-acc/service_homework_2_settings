package com.oktanc71.service_homework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class MyBroadcastreciver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MyService::class.java)
        Log.d("check", ""+intent.getIntExtra("battery", -1))
        serviceIntent.putExtra("battery", intent.getIntExtra("battery", -1))
        context.startService(serviceIntent)
    }
}