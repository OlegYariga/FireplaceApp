import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*

//TODO: использовать класс с описанием
// + непонятный вылет на этапе подключения, продебажить
@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice, val context: Context,
                    private val outputBluetoothReader: PipedReader) : Thread() {
    private var messagesString: String = ""
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

    private fun getTextFromAppLoopOrEmpty(): String {
        var retStr: String = ""
        while (outputBluetoothReader.ready()){
            retStr += outputBluetoothReader.read().toChar()
            Log.i("test", "FromAppLoop = " + retStr)
        }

        return retStr
    }

    private fun getTextFromBluetoothOrEmpty(): String {
        var retStr: String = ""

        // note: осторожно использовать, читает посимвольно и возвращает не всю строку сразу
        while (mSocket?.inputStream?.available()!! > 0) {
            retStr += mSocket?.inputStream?.read()?.toChar()
            Log.i("test", "FromBluetooth = " + retStr)
        }

        return retStr
    }

    fun sendRecieveLoop(){
        try {
            while (true) {
                // read messages from app and write to BT thread
                val appRes = this.getTextFromAppLoopOrEmpty()
                mSocket?.outputStream?.write(appRes.toByteArray())

                messagesString += this.getTextFromBluetoothOrEmpty()
                connectingProcessState = 1
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

    // TODO: забирать текущее состояние коннекта
    fun getConnectionState(): Int {
        return connectingProcessState
    }

    fun closeConnection(){
        try {
            mSocket?.close()
        }catch (i: IOException){

        }
    }

    fun receiveMessagesFromBT(): String {
        val msg = messagesString
        messagesString = ""
        return msg
    }
}