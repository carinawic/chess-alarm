package com.example.chesstheoryalarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.lang.String


class OnAlarmReceiver : BroadcastReceiver() {


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent?) {


        val sp : SharedPreferences = context.getSharedPreferences("name", AppCompatActivity.MODE_PRIVATE)
        val editor: Editor = sp.edit()
        editor.putBoolean("changeActivity2", true)
        editor.apply();
        Log.e("Editor", "has updated");

        //val preferences = context.getSharedPreferences("name", AppCompatActivity.MODE_PRIVATE)
        //val name = preferences.getString("abc", "");
        //Log.e("res", name.toString() + " is the current name from BroadcastReceiver")

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag")
        wl.acquire()
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show()

        //start chess
        val i = Intent(context, ChessAvtivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        Log.e("rec", "clicked")
        context.startActivity(i);

        // save whether we should go to chess activity in shared prefs
        //val ctx: Context = context.getApplicationContext()
        //val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        //val editor = prefs.edit()
        //editor.putBoolean("LoadNewActivity", true) // or false

        //editor.commit()


        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        var r = RingtoneManager.getRingtone(context, notification)

        r.play()

        val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        val pattern = longArrayOf(1000, 500, 1000, 500)
        var vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        vibratorService.vibrate(pattern, -1, audioAttributes)

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                vibratorService.cancel()
                r.stop()
            }
        }.start()

        Log.e("rec", "reminder")




        wl.release()
    }

}
