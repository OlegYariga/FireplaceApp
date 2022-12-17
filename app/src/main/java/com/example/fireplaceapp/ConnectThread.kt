import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.*


@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice, val context: Context) : Thread() {
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var mSocket: BluetoothSocket? = null
    var connectingProcessState: Int = 1  //TODO: статусы бесполезны, потому что это потоки

    init {
        try {
            mSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){
            connectingProcessState = -1
            Log.d("test","CANNOT CONNECT...")
        }
    }

    fun connectToDevice(tries_number: Int = 3){
        if (tries_number <= 0) throw IllegalStateException()

        try {
            Log.d("test", "Connecting...")
            mSocket?.connect()
            Log.d("test","Connected")

        }catch (i: IOException) {
            this.closeConnection()

            try {
                this.connectToDevice(tries_number - 1)
            } catch (i: java.lang.IllegalStateException) {
                this.closeConnection()
                Log.d("test", "Can not connect to device")
                Log.e("test", "error", i)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        this.connectToDevice(3)
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