package com.shersoft.android_ip

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.StreamHandler

class NetworkListnerImp(val context: Context) : StreamHandler {
    lateinit var mybroadcastListlistner: AndroidIpPlugin.mybroadcastList
    private lateinit var receiver: NetworkChangeReceiver
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
        addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        addAction("android.net.wifi.WIFI_AP_STATE_CHANGED")
        addAction("android.net.conn.CONNECTIVITY_CHANGE")
        addAction("android.net.conn.TETHER_STATE_CHANGED")
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)


    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {

        mybroadcastListlistner = object : AndroidIpPlugin.mybroadcastList {
            override fun onChange(s: String) {
                events?.success(s)
            }

        }
        receiver = NetworkChangeReceiver(mybroadcastListlistner)
        context.registerReceiver(receiver, filter)
    }

    override fun onCancel(arguments: Any?) {
        println(arguments.toString())
    }

    open class NetworkChangeReceiver(var mybroadcastListlistner: AndroidIpPlugin.mybroadcastList) :
        BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action === "android.net.wifi.WIFI_AP_STATE_CHANGED") {
                val apState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                if (apState == 13) {
                    mybroadcastListlistner.onChange("Wifi_teather_enabled")
                    // Hotspot AP is enabled
                } else {
                    mybroadcastListlistner.onChange("Wifi_teather_disabled")
                    // Hotspot AP is disabled/not ready
                }
            } else if (intent.action === "android.net.wifi.WIFI_AP_STATE_CHANGED") {
                mybroadcastListlistner.onChange("Wifi_e/d")
                // Hotspot AP is disabled/not ready
            } else if (intent.action === BluetoothDevice.ACTION_ACL_CONNECTED) {
                mybroadcastListlistner.onChange("Bluetooth_enabled")
                // Hotspot AP is disabled/not ready
            } else if (intent.action === BluetoothDevice.ACTION_ACL_DISCONNECTED) {
                mybroadcastListlistner.onChange("Bluetooth_disabled")
                // Hotspot AP is disabled/not ready
            } else if (intent.action === "android.intent.action.AIRPLANE_MODE") {
                mybroadcastListlistner.onChange("AirPlaneMode__e/d")
                // Hotspot AP is disabled/not ready
            } else if (intent.action === "android.net.conn.TETHER_STATE_CHANGED") {
                val apState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                if (apState == 13) {
                    mybroadcastListlistner.onChange("Wifi_teather_enabled")
                    // Hotspot AP is enabled
                } else {
                    mybroadcastListlistner.onChange("Teather__e/d")
                    // Hotspot AP is disabled/not ready
                }

                // Hotspot AP is disabled/not ready
            } else if (intent.action === "android.net.conn.CONNECTIVITY_CHANGE") {
                mybroadcastListlistner.onChange("Cellular_e/d")
                // Hotspot AP is disabled/not ready
            }


        }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object callback : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
//        println("==========>" + network.socketFactory)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
//        println("==========>" + network.socketFactory)
    }
}