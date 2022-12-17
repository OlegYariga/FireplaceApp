package com.example.fireplaceapp

import android.content.Context
import android.net.wifi.WifiManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPageAdapter(activity: FragmentActivity, private val tabCount: Int) : FragmentStateAdapter(activity)
{

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment
    {
        return when (position)
        {
            0 -> HomeFragment()
            1 -> PropertiesFragment()
            2 -> ScheduleFragment()

            else -> HomeFragment()
        }
    }
}