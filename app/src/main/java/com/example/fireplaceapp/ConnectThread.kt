import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*


@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice, val context: Context,
                    private val outputBluetoothReader: PipedReader,
                    private val inputBluetoothWriter: PipedWriter) : Thread() {
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var mSocket: BluetoothSocket? = null
    var connectingProcessState: Int = 1

    init {
        try {
            mSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){
            connectingProcessState = -1
            Log.e("test","CANNOT CONNECT...")
        }
    }

    fun connectToDevice(tries_number: Int = 3){
        if (tries_number <= 0) throw IllegalStateException()

        try {
            connectingProcessState = 1
            Log.d("test", "Connecting...")
            mSocket?.connect()
            Log.d("test","Connected")

        }catch (i: IOException) {
            connectingProcessState = -2
            this.closeConnection()

            try {
                this.connectToDevice(tries_number - 1)
            } catch (i: java.lang.IllegalStateException) {
                this.closeConnection()
                Log.e("test", "Can not connect to device")
            }
        }
    }

    fun sendRecieveLoop(){
        try {
            while (true) {
                // read messages from app and write to BT thread
                var i: Int = outputBluetoothReader.read()
                while (i != -1) {
                    connectingProcessState = 0
                    mSocket?.outputStream?.write(i)
                    i = outputBluetoothReader.read()
                }

                // read messages from BT and write to app thread
                var y: Int? = mSocket?.inputStream?.read()
                while (y != -1) {
                    connectingProcessState = 0
                    if (y != null) {
                        inputBluetoothWriter.write(y)
                    }
                    y = mSocket?.inputStream?.read()
                }
            }

        }catch (i: IOException) {
            connectingProcessState = -1
            Log.d("test", "pipe is broken or socket is closed.")
            this.closeConnection()
        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        this.connectToDevice(3)
        this.sendRecieveLoop()
    }

    fun getConnectionState(): Int {
        return connectingProcessState
    }

    fun closeConnection(){
        try {
            mSocket?.close()
        }catch (i: IOException){

        }
    }
}