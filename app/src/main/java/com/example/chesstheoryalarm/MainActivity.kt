package com.example.chesstheoryalarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity() {

    /*
    * TODO:
    *  everytime a switch changes, add or remove the alarm
    *  save to phone storage so the same settings remain next time you open the app
    *  if necessary, restart the alarms next time phone starts! */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        var mainLayout = findViewById<View>(R.id.innerLayout) as LinearLayout

        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))
        mainLayout.addView(LayoutInflater.from(this).inflate(R.layout.alarm_box, null))


        val btn: FloatingActionButton = findViewById<View>(R.id.fab) as FloatingActionButton
        btn.setOnClickListener { v -> onAddAlarmButtonClick(v) }

    }

    // Receiver
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            Log.e("debug", data.toString())
            //doSomeOperations()
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