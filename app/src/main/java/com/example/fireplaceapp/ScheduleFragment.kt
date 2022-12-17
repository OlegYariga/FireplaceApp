package com.example.fireplaceapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TimePicker

class ScheduleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val pickHour1 = view.findViewById(R.id.timePicker1) as NumberPicker
        pickHour1.maxValue = 10
        pickHour1.minValue = 0

        val pickMinute1 = view.findViewById(R.id.timePicker2) as NumberPicker
        pickMinute1.maxValue = 59
        pickMinute1.minValue = 1


        val pickHour2 = view.findViewById(R.id.timePicker3) as NumberPicker
        pickHour2.maxValue = 10
        pickHour2.minValue = 0

        val pickMinute2 = view.findViewById(R.id.timePicker4) as NumberPicker
        pickMinute2.maxValue = 59
        pickMinute2.minValue = 1

        return view
    }
}