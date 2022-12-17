import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.util.*


@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice, val context: Context) : Thread() {
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var mSocket: BluetoothSocket? = null
    var connectingProcessState: Int = 1

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){
            connectingProcessState = -1
            Log.d("test","CANNOT CONNECT...")
        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        // TODO: вынести в отдельный метод
        try {
            Log.d("test","Connecting???...")
            if (!mSocket!!.isConnected){
                connectingProcessState = 1
                Log.d("test","Connecting...")
                mSocket?.connect()
            }
            Log.d("test","Connected")
            connectingProcessState = 0

            //TODO: run other actions

        }catch (i: IOException){
            if (!mSocket!!.isConnected){
                connectingProcessState = -1
                Log.d("test","Can not connect to device")
                Log.e("test", "error", i)
                closeConnection()
            }else {
                connectingProcessState = 0
                Log.d("test", "Already connected to device")
            }
        }
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