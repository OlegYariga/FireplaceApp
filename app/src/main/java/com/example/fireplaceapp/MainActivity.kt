package com.example.fireplaceapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_button.setOnClickListener(this)
        stop_button.setOnClickListener(this)

        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener {group, checkedId ->
                if (checkedId == 2131296744) fire_view.changeColour(0)
                if (checkedId == 2131296745) fire_view.changeColour(1)
                if (checkedId == 2131296746) fire_view.changeColour(2)
            })

    }

    override fun onClick(v: View?) {
        if (v == null) return
        when (v.id) {
            R.id.start_button -> fire_view.startFire()
            R.id.stop_button -> fire_view.stopFire()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}