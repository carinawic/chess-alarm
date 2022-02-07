package com.example.chesstheoryalarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity() {


    /*
    * TODO:
    *  alarm trigger every day
    *  switch alarms on off
    *  turn on screen in powerManager wakelock when the alarm rings
    *  sound
    *  store when phone closes
    * */

    /*
    * TODO:
    *  everytime a switch changes, add or remove the alarm
    *  save to phone storage so the same settings remain next time you open the app
    *  if necessary, restart the alarms next time phone starts! */


    var pendingIntents: ArrayList<PendingIntent> = ArrayList<PendingIntent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val btn: FloatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        btn.setOnClickListener { v -> onAddAlarmButtonClick(v) }

    }

    private fun onClickAlarmButton(v: View) {
        // all click listeners should end up here
        Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
    }


    private fun createWidgetForAlarm(time: String){
        var innerLayout = findViewById<View>(R.id.innerLayout) as LinearLayout

        val inflater = this.layoutInflater
        val v: View = LayoutInflater.from(this).inflate(R.layout.alarm_box, null)

        innerLayout.addView(v)

        val sv = findViewById<ScrollView>(R.id.scrollvewid) as ScrollView
        val il = sv.findViewById<LinearLayout>(R.id.innerLayout) as LinearLayout
        val cl = il.findViewById<ConstraintLayout>(R.id.widgetid) as ConstraintLayout

        val bt = cl.findViewById<Button>(R.id.timeButton) as Button
        val sw = cl.findViewById<Switch>(R.id.alarmSwitch) as Switch

        sw.setOnClickListener { v -> onClickAlarmButton(v) }
        bt.setOnClickListener { v -> onClickAlarmButton(v) }

        bt.text = time

        /*

        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        */
    }

    /*From StackOverflow: If you want to set multiple alarms (repeating or single), then you just need to create their PendingIntents with different requestCode. If requestCode is the same, then the new alarm will overwrite the old one.*/
    private fun setAlarm() {

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

        cal_alarm.add(Calendar.SECOND, 10)
        val myIntent = Intent(this, ChessAvtivity::class.java)
        val id = System.currentTimeMillis().toInt()
        val am = getSystemService(ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getActivity(this,
                id, myIntent, 0) // id is unique

        pendingIntents.add(pendingIntent) // issue that this is global?

        am[AlarmManager.RTC_WAKEUP, cal_alarm.timeInMillis] = pendingIntent

    }

    // Receiver
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            val hour = data?.getStringExtra("hour")
            val minute = data?.getStringExtra("minute")

            Log.e("debug", hour.toString())

            createWidgetForAlarm("$hour:$minute")
            //createAlarm(input)
        }
    }



    fun onAddAlarmButtonClick(v: View?) {
        val intent = Intent(this@MainActivity, TimeActivity::class.java)
        resultLauncher.launch(intent) //caller
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}