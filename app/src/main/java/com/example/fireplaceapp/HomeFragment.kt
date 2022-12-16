package com.example.fireplaceapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.fragment_home.*

import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val btn_start = view.findViewById(R.id.start_button) as Button
        btn_start.setOnClickListener(this)

        val rd_group = view.findViewById(R.id.radioGroup) as RadioGroup
        rd_group.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton? = view?.findViewById(checkedId)
                when (radio) {
                    radioButton3 -> {
                        fire_view.changeColour(0)
                    }

                    radioButton4 -> {
                        fire_view.changeColour(1)
                    }

                    radioButton5 -> {
                        fire_view.changeColour(2)
                    }
                }
            })

        return view
    }

    override fun onClick(v: View?) {
        if (v == null) return
        when (v.id) {
            R.id.start_button -> {
                if (start_button.text == "Start"){
                    start_button.text = "Starting..."
                    start_button.setBackgroundColor(getResources().getColor(R.color.Red))

                    fire_view.startFire()
                    start_button.text = "Stop"
                }else{
                    start_button.text = "Stopping..."
                    start_button.setBackgroundColor(getResources().getColor(R.color.Green))

                    fire_view.stopFire()
                    start_button.text = "Start"
                }
            }
        }
    }
}