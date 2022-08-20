package com.oktanc71.service_homework

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherActivityInfo
import android.graphics.Color
import android.media.RingtoneManager
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MyService : Service() {
    var battery: Int = 0
    var isPlugged: Boolean = false
    private val channelId = "com.oktanc71.service_homework"
    private val description = "notification"
    lateinit var settings: SharedPreferences

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        settings = getSharedPreferences(Consts.APP_SETTINGS, MODE_PRIVATE)
        val status: Int = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        isPlugged = status == BatteryManager.BATTERY_STATUS_CHARGING

        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
        battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        Log.d("check", "service: $battery")
        checkBattery()
        return START_STICKY
    }


    fun checkBattery() {


        val intent = Intent(this, MainActivity::class.java)
        val resultIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
        var text = "Battery level is $battery"
        if (isPlugged)
            text += " plugged"
        else text += " unplugged"

        settings.getString("battery", "-").let {
            if ((battery > it?.split('-')?.get(0)?.toInt()!!)
                        and (battery  < it.split('-')[1].toInt()))
            {
                if(settings.getBoolean("show_window", false)){
                    MaterialAlertDialogBuilder(Consts.CONTEXT)
                        .setTitle("батарея")
                        .setMessage("батарея разряжена")
                        .setNegativeButton("закрыть") { dialog, which ->
                        }
                        .setPositiveButton("ok") { dialog, which ->
                        }
                        .show()
                }
                text += " разряжена"

            }
        }



        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Battery level")
            .setContentText(text)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)

        if (settings.getBoolean("sound", false))
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        if (settings.getBoolean("vibration", false))
            builder.setVibrate(longArrayOf(500, 700, 800))

        val notification = builder.build()
        settings.getString("time", "-").let {
            if (!((LocalDateTime.now().hour > it?.split('-')?.get(0)?.toInt()!!)
                        and (LocalDateTime.now().hour < it.split('-')[1].toInt()))
            )
                notificationManager.notify(1, notification)
        }
//
//        if(battery <70)
//        {
//
//            val i = Dialog(this)
//            i.show();
//        }
//        startForeground(1, notification);
    }
}