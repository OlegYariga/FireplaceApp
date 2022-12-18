package com.example.fireplaceapp

import ConnectThread
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter
import java.lang.reflect.Method
import java.util.*

// TODO: сюда положить сообщение от BT
data class BtMessage(var text: String, var status_codes: Array<Int>){}

// TODO: перенести сюдя коды ошибок
class Constants{
    companion object {
        // Message types sent from the BluetoothChatService Handler
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_TOAST = 5
        var MESSAGE_TYPE_SENT = 0
        var MESSAGE_TYPE_RECEIVED = 1
    }
}


class BtConnection(private val adapter: BluetoothAdapter) {
    private var inited: Boolean = false

    /*
        Return codes:

        -20 = no device found with name "FireplaceDevice"
        -30 = bluetooth adapter is disabled
        --------------
        -1 = can't connect to BT device
        -2 = retry connections
        1 = connecting to BT device
        0 = connected to BT device

        0 = connected

    */
    lateinit var cThread: ConnectThread
    var connectionState: Int = 1 //default state - connecting
    var deviceBTAddress: String = ""

    var outputBluetoothReader: PipedReader? = null
    var outputBluetoothWriter: PipedWriter? = null

    var inputBluetoothReader: PipedReader? = null
    var inputBluetoothWriter: PipedWriter? = null

    var outputMessages: String = ""
    var inputMessages: String = ""

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
    private fun checkRequirements(): Int {
        Log.i("test", "Checking for permissions.")
        if (!adapter.isEnabled) {
            return -30
        }

        val pairedDevices: Set<BluetoothDevice> = adapter.bondedDevices
        if (pairedDevices.size > 0) {
            for (device: BluetoothDevice in pairedDevices) {
                if (device.name == "FireplaceDevice"){
                    deviceBTAddress = device.address
                }
            }
        }

        Log.i("test", "Device address: " + deviceBTAddress)
        if (deviceBTAddress.isEmpty()){
            Log.i("test", "No fireplace")
            return -20
        }

        Log.i("test", "Permissions are ok.")
        return 0
    }
    private fun createCommunicationPipes(){
        Log.e("test", "Create communication pipes.")

        // for writing to BT
        outputBluetoothReader = PipedReader()
        outputBluetoothWriter = PipedWriter()

        try {
            outputBluetoothWriter!!.connect(outputBluetoothReader)
        } catch (e: IOException) {
            Log.e("test", "Cannot connect Reader/Writer with thread!")
        }

        // for reading from BT
        inputBluetoothReader = PipedReader()
        inputBluetoothWriter = PipedWriter()

        try {
            inputBluetoothReader!!.connect(inputBluetoothWriter)
        } catch (e: IOException) {
            Log.e("test", "Cannot connect Reader/Writer with thread!")
        }
    }

    @SuppressLint("MissingPermission")
    fun communicate(context: Context, message: String): Int {
        outputMessages += message

        val requirements: Int = this.checkRequirements()
        if (requirements != 0){
            return requirements
        }

        val device = adapter.getRemoteDevice(deviceBTAddress)
        if (!isConnected(device)){
            connectionState = 1  // TODO: replace all numerics to literal
            this.createCommunicationPipes()
            Log.e("test", "Create device thread.")

            inited = true
            device.let {
                cThread = ConnectThread(it, context, outputBluetoothReader!!)
                cThread.start()
//                inputBluetoothReader = cThread.getReader()
            }
        }

        var btMsg: String = "notinited"
        if (inited){
            btMsg = cThread.receiveMessagesFromBT()
        }

        try{
            Log.e("test", "Send message.")
            outputBluetoothWriter?.write(outputMessages) // TODO: передавать message
//
            Log.e("test", "Read messages.") // TODO: дополнить
            inputMessages = btMsg

        }catch (i: IOException){
            Log.e("test", "Pipe read is dead.") // TODO: дополнить
            return -1
        }
        outputMessages = ""

        Log.i("test", "InputMessage = " + inputMessages + btMsg)
        inputMessages = ""
        connectionState = 0 // TODO: вычитывать статус из Pipe

        return connectionState
    }
}