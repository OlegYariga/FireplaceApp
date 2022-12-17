package com.example.fireplaceapp

import ConnectThread
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import java.lang.reflect.Method
import java.util.*


class BtConnection(private val adapter: BluetoothAdapter) {
    /*
    Return codes:

    -20 = no device found with name "FireplaceDevice"
    -30 = bluetooth adapter is disabled

    0 = connected
     */
    lateinit var cThread: ConnectThread
    var connectionState: Int = 1 //default state - connecting
    var deviceBTAddress: String = ""

    @SuppressLint("MissingPermission")
    fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod(
                "isConnected"
            )
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(mac: String, context: Context): Int {
        Log.i("test", "enter to connect")
        if (!adapter.isEnabled) {
            return -30
        }

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

        val device = adapter.getRemoteDevice(deviceBTAddress)
        val ddd = device.name + "\n" + device.address;
        Log.i("test", "connecting to: " + ddd)

        val lastConnectionState = connectionState

        val uuid = "00001101-0000-1000-8000-00805F9B34FB"

        val mSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))

        if (isConnected(device)){
            Log.d("test", "Already connected to = " + device.name)
            return 0
        }

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