package com.example.fireplaceapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : AppCompatActivity() {

    private lateinit var btConnection: BtConnection
    private val REQUEST_ENABLE_BT: Int = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTabBar()

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

        if (bluetoothAdapter == null) {
            // TODO: LOG this
            // Device doesn't support Bluetooth
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                Log.e("test", "no permissions 1 - OK!")

            } else {
                Log.e("test", "no permissions 1")
                Log.e("test",
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        .toString()
                )

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_PHONE_STATE), 0
                )
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("test", "no permissions")
                Log.e("test",
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        .toString()
                )

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2
                )
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        init_bluetooth()
    }

    private fun init_bluetooth(){
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = btManager.adapter
        btConnection = BtConnection(btAdapter)
    }

    private fun setUpTabBar() {
        val adapter = TabPageAdapter(this, tabLayout.tabCount)
        adapter.createFragment(0)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                // TODO: вынести это отсюда! Подключаться нужно при выполнении каких-то действий
                var res: Int = btConnection.connect("00:11:22:33:FF:EE", applicationContext)

                if (res == 1) {
                    Toast.makeText(applicationContext, "Подключаюсь к устройству. Ждите...", Toast.LENGTH_LONG).show()

                }else if (res == -1){
                    Toast.makeText(applicationContext, "Не могу найти устройство FireplaceDevice. Проверьте, работает ли оно...", Toast.LENGTH_LONG).show()

                }else if (res == -20) {
                    Toast.makeText(applicationContext, "Подключитесь к устройству 'FireplaceDevice' в настройках Bluetooth! (3 сек)", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                    }, 3000)

                }else if (res == -30) {
                    Toast.makeText(applicationContext, "Включите адаптер Bluetooth! (3 сек)", Toast.LENGTH_LONG).show()
                    Handler().postDelayed({
                        startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                    }, 3000)
                }

                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab)
            {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}