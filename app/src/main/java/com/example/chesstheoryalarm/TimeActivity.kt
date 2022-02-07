package com.example.chesstheoryalarm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


/*
* TODO:
*  alarm trigger every day
*  switch alarms on off
*  turn on screen in powerManager wakelock when the alarm rings
*  sound
*  store when phone closes
* */

class TimeActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        val btn: Button = findViewById<View>(R.id.createAlarmButton) as Button
        btn.setOnClickListener { v -> onCreateAlarmButtonclick(v) }
    }

    fun onCreateAlarmButtonclick(v: View) {
        val tpck: TimePicker = findViewById<View>(R.id.timePicker1) as TimePicker
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.e("debug", tpck.hour.toString())
            Log.e("debug", tpck.minute.toString())
        }
        start()

        val intent = Intent()
        intent.putExtra("MESSAGE", "timedata");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    /*From StackOverflow: If you want to set multiple alarms (repeating or single), then you just need to create their PendingIntents with different requestCode. If requestCode is the same, then the new alarm will overwrite the old one.*/
    private fun start() {
        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        val dat = Date()
        val cal_alarm: Calendar = Calendar.getInstance()

        /*
        val cal_now: Calendar = Calendar.getInstance()
        cal_now.setTime(dat)
        cal_alarm.setTime(dat)
        cal_alarm.set(Calendar.HOUR_OF_DAY, 10)
        cal_alarm.set(Calendar.MINUTE, 14)
        cal_alarm.set(Calendar.SECOND, 30)
        if (cal_alarm.before(cal_now)) {
            cal_alarm.add(Calendar.DATE, 1)
        }
        */
        cal_alarm.add(Calendar.SECOND, 5) // in
        cal_alarm.add(Calendar.SECOND, 10) // in 5 sec

        val myIntent = Intent(this, ChessAvtivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this,
                12345, myIntent, 0)

        val am = getSystemService(ALARM_SERVICE) as AlarmManager

        am[AlarmManager.RTC_WAKEUP, cal_alarm.timeInMillis] = pendingIntent


    }

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show()
        }
    }

}