package com.example.chesstheoryalarm

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log


class AlarmService : WakeIntentService("AlarmService") {
    override fun doReminderWork(intent: Intent?) {
        Log.e("rec", "reminder2222")



    }
}