package com.oktanc71.service_homework

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.oktanc71.service_homework.databinding.ActivityMainBinding
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var reciver: MyBroadcastreciver
    lateinit var settings: SharedPreferences
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = getSharedPreferences(Consts.APP_SETTINGS, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Consts.CONTEXT = this
        syncSettings()
        reciver = MyBroadcastreciver()
        registerReceiver(MyBroadcastreciver(), IntentFilter(Consts.BROADCAST_ACTION));
        checkBattery()
        binding.btn.setOnClickListener {
            settings.edit()
                .putBoolean("sound", binding.sound.isChecked)
                .putBoolean("vibration", binding.vibration.isChecked)
                .putString("time", "${binding.timeStart.text}-${binding.timeEnd.text}")
                .putString("battery", "${binding.batteryStart.text}-${binding.batteryEnd.text}")
                .putBoolean("show_window", binding.windowShow.isChecked)
                .apply()
            Toast.makeText(this, "settings save", Toast.LENGTH_SHORT).show()
        }
    }


    private fun syncSettings() {
        binding.sound.isChecked = settings.getBoolean("sound", false)
        binding.vibration.isChecked = settings.getBoolean("vibration", false)
        settings.getString("time", "-").let {
            binding.timeStart.setText(it?.split('-')?.get(0))
            binding.timeEnd.setText(it?.split('-')?.get(1))
        }
        settings.getString("battery", "-").let {
            binding.batteryStart.setText(it?.split('-')?.get(0))
            binding.batteryEnd.setText(it?.split('-')?.get(1))
        }
        binding.windowShow.isChecked = settings.getBoolean("show_window", false)
    }

    fun checkBattery() {
        val intent = Intent()
        intent.action = Consts.BROADCAST_ACTION
        sendBroadcast(intent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(MyBroadcastreciver(), IntentFilter(Consts.BROADCAST_ACTION));
    }
}