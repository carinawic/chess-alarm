package com.example.chesstheoryalarm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class TimeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        val btn: Button = findViewById<View>(R.id.createAlarmButton) as Button
        btn.setOnClickListener { v -> onCreateAlarmButtonclick(v) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onCreateAlarmButtonclick(v: View) {
        val tpck: TimePicker = findViewById<View>(R.id.timePicker1) as TimePicker
        //Log.e("debug", tpck.hour.toString())
        //Log.e("debug", tpck.minute.toString())
        val intent = Intent()
        intent.putExtra("hour", tpck.hour.toString());
        intent.putExtra( "minute", tpck.minute.toString())
        setResult(Activity.RESULT_OK, intent);
        finish()
    }

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show()
        }
    }
}