package com.example.chesstheoryalarm


import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log


abstract class WakeIntentService(name: String?) : IntentService(name) {
    abstract fun doReminderWork(intent: Intent?)
    override fun onHandleIntent(intent: Intent?) {
        try {

            doReminderWork(intent)
        } finally {
            getLock(this)!!.release()
        }
    }

    companion object {
        const val LOCK_NAME_STATIC = "com.example.alarmexample"
        private var lockStatic: WakeLock? = null
        fun acquireStaticLock(context: Context) {
            Log.e("rec", "start lock")
            getLock(context)!!.acquire()
        }

        @SuppressLint("InvalidWakeLockTag")
        @Synchronized
        private fun getLock(context: Context): WakeLock? {
            if (lockStatic == null) {
                val powManager = context
                        .getSystemService(Context.POWER_SERVICE) as PowerManager
                lockStatic = powManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        LOCK_NAME_STATIC)
                lockStatic?.setReferenceCounted(true)
            }
            return lockStatic
        }
    }
}