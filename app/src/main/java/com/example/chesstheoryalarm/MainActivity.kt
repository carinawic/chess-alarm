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
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity(){


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


    private var alarms = ArrayList<Alarm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val btn: FloatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        btn.setOnClickListener { v -> onAddAlarmButtonClick(v) }

    }

    private fun createWidgetForAlarm(hour: String, minute:String){
        val time = "$hour:$minute"

        var innerLayout = findViewById<View>(R.id.innerLayout) as LinearLayout

        val v: View = LayoutInflater.from(this).inflate(R.layout.alarm_box, null)

        innerLayout.addView(v)

        val sv = findViewById<ScrollView>(R.id.scrollvewid) as ScrollView
        val il = sv.findViewById<LinearLayout>(R.id.innerLayout) as LinearLayout
        val cl = il.findViewById<ConstraintLayout>(R.id.widgetid) as ConstraintLayout

        val bt = cl.findViewById<Button>(R.id.timeButton) as Button
        val sw = cl.findViewById<Switch>(R.id.alarmSwitch) as Switch

        bt.text = time
        // put onclicklisteners on ALL buttons at the same time
        for(child in il.children){
            val buttonToClick = child.findViewById<Button>(R.id.timeButton) as Button

            val switchToClick = child.findViewById<Switch>(R.id.alarmSwitch) as Switch
            buttonToClick.setOnClickListener(){
                //Toast.makeText(this@MainActivity, "You clicked a button.", Toast.LENGTH_SHORT).show()

            }
            switchToClick.setOnClickListener(){

                var message: String

                if(switchToClick.isChecked){
                    message = "Alarm is on"
                    Log.e("debug", "we want to switch on the alarm with the id : " + switchToClick.parent.hashCode())
                    startAlarm(cl.hashCode())
                }else{
                    message = "Alarm is off"
                    Log.e("debug", "we want to switch off the alarm with the id : " + switchToClick.parent.hashCode())
                    stopAlarm(cl.hashCode()) // should be same as switchToClick.parent.hashCode()
                }

                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()

            }
        }

        //bt.text = time


        addAlarm(cl.hashCode(), false, minute, hour)

    }

    // https://developer.android.com/training/scheduling/alarms
    private fun addAlarm(hash: Int, onoff: Boolean, minute: String, hour:String){

        // set placeholder pendingIntent
        val myIntent = Intent(this, ChessAvtivity::class.java)
        val id = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(this,
                id, myIntent, 0)


        val calAlarm: Calendar = Calendar.getInstance()

        calAlarm.set(Calendar.HOUR_OF_DAY, hour.toInt())
        calAlarm.set(Calendar.MINUTE, minute.toInt())
        calAlarm.set(Calendar.SECOND, 0)

        Log.e("debug", "setting alarm at minute ${minute.toInt().toString()}")
        Log.e("debug", "setting alarm at hour ${hour.toInt().toString()}")
        //calAlarm.add(Calendar.SECOND, 10) // here we use the parameter time

        alarms.add(Alarm(hash, pendingIntent, onoff, calAlarm.timeInMillis))


    }

    private fun startAlarm(hash:Int) {

        for (alarm in alarms){
            if (alarm.id == hash){

                alarm.active=true

                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                am[AlarmManager.RTC_WAKEUP, alarm.timeToGoOff] = alarm.pendingIntent // unique id in alarm manger!
                break

            }
        }

        printAllAlarms()

    }

    private fun stopAlarm(hash : Int){

        for (alarm in alarms){
            if (alarm.id == hash){
                alarm.active=false
                break
            }
        }
        Log.e("debug", "cancelling alarm with id : $hash")
        printAllAlarms()

    }

    private fun printAllAlarms(){
        for (alarm in alarms){
            alarm.printme()
        }
    }


    /*From StackOverflow: If you want to set multiple alarms (repeating or single), then you just need to create their PendingIntents with different requestCode. If requestCode is the same, then the new alarm will overwrite the old one.*/


    // Receiver
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            var hour = data?.getStringExtra("hour")
            var minute = data?.getStringExtra("minute")

            Log.e("debug", hour.toString())

            // change 3:30 and 12:5 to 03:30 and 12:05
            if(hour!!.length == 1){
                hour = "0$hour"
            }
            if(minute!!.length == 1){
                minute = "0$minute"
            }

            //addAlarm(Calendar.getInstance().timeInMillis, false, minute, hour)
            createWidgetForAlarm(hour, minute)
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

    class Alarm(val id: Int, val pendingIntent: PendingIntent, var active: Boolean, val timeToGoOff: Long){

        fun turnOff(){
            active = !active
        }

        fun printme(){
            Log.e("debug", "id $id pendingIntent $pendingIntent active $active timeToGoOff $timeToGoOff")
        }

    }

}