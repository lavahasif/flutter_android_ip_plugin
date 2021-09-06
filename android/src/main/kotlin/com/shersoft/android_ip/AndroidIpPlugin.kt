package com.shersoft.android_ip


import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.NonNull
import com.shersoft.test.util.MyIp
import com.shersoft.test.util.network_interfac
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** AndroidIpPlugin */
class AndroidIpPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var receiver: NetworkChangeReceiver
    lateinit var mybroadcastListlistner: mybroadcastList

    interface mybroadcastList {
        fun onChange(typeevent: String)
    }

    lateinit var myIp: MyIp
    lateinit var sharefile: ShareFile
    lateinit var context: Context

    init {


    }

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var echannel: EventChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        myIp = MyIp(context)
        sharefile = ShareFile(context)
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "android_ip")
        echannel = EventChannel(flutterPluginBinding.binaryMessenger, "networklistner")
        channel.setMethodCallHandler(this)
        setEventChannel()

    }

    private fun setEventChannel() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction("android.net.wifi.WIFI_AP_STATE_CHANGED")
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction("android.net.conn.TETHER_STATE_CHANGED")
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)


        }





        echannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {

                mybroadcastListlistner = object : mybroadcastList {
                    override fun onChange(s: String) {
                        events?.success(s)
                    }

                }
                receiver = NetworkChangeReceiver(mybroadcastListlistner)
                context.registerReceiver(receiver, filter)
            }

            override fun onCancel(arguments: Any?) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val method = NetWork_Interface_enum.valueOf(call.method)
        when (method) {

            NetWork_Interface_enum.Wifi -> result.success(myIp.getdeviceIpAddress_Wifi())
            NetWork_Interface_enum.Wifi_tether -> result.success(myIp.getIPAddress_Util(true))
            NetWork_Interface_enum.USB_tether -> result.success(myIp.getIpInterface(network_interfac.rndis0.name))
            NetWork_Interface_enum.Blue_ther -> result.success(myIp.getIpInterface(network_interfac.bt_pan.name))
            NetWork_Interface_enum.Cellular1 -> result.success(myIp.getIpInterface(network_interfac.rmnet_data1.name))
            NetWork_Interface_enum.Cellular2 -> result.success(myIp.getIpInterface(network_interfac.rmnet_data2.name))
            NetWork_Interface_enum.All -> result.success(myIp.getNetworkIp4LoopbackIps().toString())
            NetWork_Interface_enum.getPlatformVersion -> result.success("Android ${Build.VERSION.RELEASE}")
            NetWork_Interface_enum.shareself -> sharefile.shareAppAsAPK(context)
            NetWork_Interface_enum.Private -> myIp.getIpAddress_Private(object :
                MyIp.VolleyListner {
                override fun Onresponse(data: String) {

                    result.success(data)
                }
            })

            else -> result.notImplemented()
        }
//        if (call.method == "getPlatformVersion") {
//            result.success("Android ${android.os.Build.VERSION.RELEASE}")
//        } else if (call.method == "getIpaddress") {
//            result.success(myIp.getIPAddress_Util(true))
//        } else {
//            result.notImplemented()
//        }
    }

    private fun getIpInterface(result: Result) {
        val split = myIp.getWifiIp(context)?.split(",")
        split?.contains("rmnet_data2")
        result.success(split?.contains("rmnet_data2"))
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        echannel.setStreamHandler(null)
    }

    open class NetworkChangeReceiver(var mybroadcastListlistner: mybroadcastList) :
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


//            StringBuilder().apply {
//                append("Action: ${intent.action}\n")
//                append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
//                toString().also { log ->
//                    Log.d(TAG + "---------->", log)
//                    Toast.makeText(context, log, Toast.LENGTH_LONG).show()
//                }
//            }
        }
    }
}

enum class NetWork_Interface_enum {
    Wifi, Wifi_tether, USB_tether, Blue_ther, Cellular1, Cellular2, All, getPlatformVersion, Private, shareself
}

private const val TAG = "MyBroadcastReceiver"

