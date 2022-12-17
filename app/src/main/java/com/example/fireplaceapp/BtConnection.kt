package com.example.fireplaceapp

import ConnectThread
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat


class BtConnection(private val adapter: BluetoothAdapter) {
    /*
    Return codes:

    -20 = no device found with name "FireplaceDevice"
    -30 = bluetooth adapter is disabled

    0 = connected
     */

    lateinit var cThread: ConnectThread
    var connectionState: Int = 1 //default state - connecting

    @SuppressLint("MissingPermission")
    fun connect(mac: String, context: Context): Int {
        Log.i("test", "enter to connect")
        if (!adapter.isEnabled) {
            return -30
        }

        var deviceBTAddress: String = ""
        val pairedDevices: Set<BluetoothDevice> = adapter.bondedDevices
        if (pairedDevices.size > 0) {
            for (device: BluetoothDevice in pairedDevices) {
                if (device.name == "FireplaceDevice"){
                    deviceBTAddress = device.address
                }
//                Log.i("test", deviceBTAddress)
//                Log.i("test", device.name)
            }
        }

        Log.i("test", deviceBTAddress)
        if (deviceBTAddress.isEmpty()){
            Log.i("test", "No fireplace")
            return -20
        }

        val device = adapter.getRemoteDevice(mac)

        val lastConnectionState = connectionState

        device.let {
            cThread = ConnectThread(it, context)
            cThread.start()
            connectionState = cThread.getConnectionState()
        }

        if (lastConnectionState == -1){
            connectionState = lastConnectionState
        }

        return connectionState
    }
}