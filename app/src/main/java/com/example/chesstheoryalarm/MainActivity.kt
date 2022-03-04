package com.example.chesstheoryalarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity(){

    private var alarms = ArrayList<Alarm>()

    override fun onResume() {
        super.onResume()

        Log.e("res", "resume!!!")
        val prefs = getSharedPreferences("name", 0)
        val loadNewActivity = prefs.getBoolean("changeActivity2", false)

        if (loadNewActivity) {
            val intent = Intent(this, ChessAvtivity::class.java)
            startActivity(intent)
            Log.e("loadNewActivity2", loadNewActivity.toString())
        } else {

            Log.e("res", "normal!!!")
            // Do normal startup
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val btn: FloatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        btn.setOnClickListener { v -> onAddAlarmButtonClick(v) }
    }

    private fun createWidgetForAlarm(hour: String, minute: String){
        val time = "$hour:$minute"

        var innerLayout = findViewById<View>(R.id.innerLayout) as LinearLayout

        // create bunch of views v, one for each logical alarm in the list
        val v: View = LayoutInflater.from(this).inflate(R.layout.alarm_box, null)

        innerLayout.addView(v)

        Log.e("debuggers", "hash is ")
        Log.e("debuggers", v.hashCode().toString())

        if (v is ViewGroup) {
            val viewGroup = v as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)

                if(child is MaterialButton)
                {
                    Log.e("debuggers", "found button!")
                    Log.e("debuggers", child.toString())

                    child.text = time

                    child.setOnClickListener(){
                        Toast.makeText(
                                this@MainActivity,
                                "You clicked a button.",
                                Toast.LENGTH_SHORT
                        ).show()

                    }

                }
                if(child is Switch)
                {
                    Log.e("debuggers", "found switch!")
                    Log.e("debuggers", child.toString())

                    child.setOnClickListener(){

                        var message: String

                        if(child.isChecked){
                            message = "Alarm is on"
                            Log.e(
                                    "debug",
                                    "we want to switch on the alarm with the id : " + v.hashCode()
                            )
                            startAlarm(v.hashCode())
                        }else{
                            message = "Alarm is off"
                            Log.e(
                                    "debug",
                                    "we want to switch off the alarm with the id : " + v.hashCode()
                            )
                            stopAlarm(v.hashCode()) // should be same as switchToClick.parent.hashCode()
                        }

                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        addAlarm(v.hashCode(), false, minute, hour)

    }

    // https://developer.android.com/training/scheduling/alarms
    private fun addAlarm(hash: Int, onoff: Boolean, minute: String, hour: String){

        val i = Intent(this, OnAlarmReceiver::class.java)
        Log.e("rec", "clicked")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, i,
                PendingIntent.FLAG_ONE_SHOT)

        val calAlarm: Calendar = Calendar.getInstance()

        calAlarm.timeInMillis = System.currentTimeMillis()
        calAlarm.set(Calendar.HOUR_OF_DAY, hour.toInt())
        calAlarm.set(Calendar.MINUTE, minute.toInt())
        calAlarm.set(Calendar.SECOND, 0)
        calAlarm.set(Calendar.MILLISECOND, 0);

        Log.e("setalarm", "setting alarm at minute ${minute.toInt().toString()}")
        Log.e("setalarm", "setting alarm at hour ${hour.toInt().toString()}")
        //calAlarm.add(Calendar.SECOND, 10) // here we use the parameter time

        // if alarm time has already passed, increment day by 1
        //if (calAlarm.timeInMillis <= System.currentTimeMillis()) {
        //    calAlarm.set(Calendar.DAY_OF_MONTH, calAlarm.get(Calendar.DAY_OF_MONTH) + 1);
        //}

        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        alarms.add(Alarm(hash, pendingIntent, onoff, calAlarm.timeInMillis, am))

    }

    private fun startAlarm(hash: Int) {

        for (alarm in alarms){
            if (alarm.id == hash){

                alarm.active=true

                //am[AlarmManager.RTC_WAKEUP, alarm.timeToGoOff] = alarm.pendingIntent // unique id in alarm manger!
                //break
                //am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, alarm.timeToGoOff - System.currentTimeMillis(), alarm.pendingIntent );

                alarm.startalarm()

                break

            }
        }

        printAllAlarms()

    }

    private fun stopAlarm(hash: Int){

        for (alarm in alarms){
            if (alarm.id == hash){
                alarm.active=false

                alarm.stopalarm()

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

    class Alarm(
            val id: Int,
            val pendingIntent: PendingIntent,
            var active: Boolean,
            val timeToGoOff: Long,
            val am: AlarmManager
    ){

        fun turnOff(){
            active = !active
        }

        val RUN_DAILY = (24 * 60 * 60 * 1000).toLong()

        fun startalarm(){
            am?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                this.timeToGoOff,
                //System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY,
                this.pendingIntent
            )
            am[AlarmManager.RTC_WAKEUP, timeToGoOff] = pendingIntent
        }

        fun stopalarm(){
            am?.cancel(pendingIntent)
        }

        fun printme(){
            Log.e(
                    "debug",
                    "id $id pendingIntent $pendingIntent active $active timeToGoOff $timeToGoOff"
            )
        }
    }
}