package com.shersoft.android_ip

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shersoft.android_ip.util.*
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MethodCallImpl(
    val context: Context,

    val connecteddevice: ConnectedDevice,
    val enableDevice: EnableDevice,
    val sharefile: ShareFile,
    val myIp: MyIp
) : MethodChannel.MethodCallHandler {

    var mactivity: Activity? = null
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        connecteddevice

        val method = NetWork_Interface_enum.valueOf(call.method)
//        println("==================>" + connecteddevice.getSsid());
        when (method) {

            NetWork_Interface_enum.Wifi -> result.success(myIp.getdeviceIpAddress_Wifi())
            NetWork_Interface_enum.Wifi_tether -> result.success(
                myIp.getIpInterface(
                    network_interfac.wlan1.name
                )
            )
            NetWork_Interface_enum.Wifi_tetherorWifi -> result.success(myIp.getIPAddress_Util(true))
            NetWork_Interface_enum.USB_tether -> result.success(myIp.getIpInterface(network_interfac.rndis0.name))
            NetWork_Interface_enum.Blue_ther -> result.success(myIp.getIpInterface(network_interfac.bt_pan.name))
            NetWork_Interface_enum.Cellular1 -> result.success(myIp.getIpInterface(network_interfac.rmnet_data1.name))
            NetWork_Interface_enum.Cellular2 -> result.success(myIp.getIpInterface(network_interfac.rmnet_data2.name))
            NetWork_Interface_enum.All -> result.success(myIp.getNetworkIp4LoopbackIps().toString())
            NetWork_Interface_enum.getPlatformVersion -> result.success("Android ${Build.VERSION.RELEASE}")
            NetWork_Interface_enum.shareself -> result.success(sharefile.shareAppAsAPK(context))
            NetWork_Interface_enum.Private -> myIp.getIpAddress_Private(object :
                MyIp.VolleyListner {
                override fun Onresponse(data: String) {

                    result.success(data)
                }
            })

            NetWork_Interface_enum.ConnectedList -> result.success(
                connecteddevice.pingHost(
                    myIp.getIPAddress_Util(
                        true
                    ), 1000
                ).toString()
            )
            NetWork_Interface_enum.isWifiEnabled -> result.success(connecteddevice.isWifiEnabled())
            NetWork_Interface_enum.isHotspotEnabled -> result.success(connecteddevice.isHotspotEnabled())
            NetWork_Interface_enum.isConnectedWifi -> result.success(connecteddevice.isWifiConnected())
            NetWork_Interface_enum.OpenSettings -> connecteddevice.openAppSettings(
                context,
                object : AndroidIpPlugin.OpenAppSettingsSuccessCallback {
                    override fun onSuccess(appSettingsOpenedSuccessfully: Boolean) {
                        result.success(
                            appSettingsOpenedSuccessfully.toString()
                        );
                    }

                },
                object : AndroidIpPlugin.ErrorCallback {
                    override fun onError(errorCode: String?, errorDescription: String?) {
                        result.error(
                            errorCode,
                            errorDescription, null
                        )
                    }
                })
            NetWork_Interface_enum.EnablePermission -> setPermissions(result)
            NetWork_Interface_enum.getssid -> result.success(connecteddevice.getSsid())
            NetWork_Interface_enum.networkresult -> settypeSafe(result)
            NetWork_Interface_enum.EnableWifi -> enableDevice.setWifiEnable()
            NetWork_Interface_enum.DisableWifi -> enableDevice.setWifiDisable()
            NetWork_Interface_enum.SetHotspotEnable -> enableDevice.SetHotspotEnable()
            NetWork_Interface_enum.SetHotspotDisable -> enableDevice.turnOffHotspot()
            NetWork_Interface_enum.IsLocationEnabled -> result.success(
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == 0
                ) true else false
            )
            else -> result.notImplemented()
        }
    }


    private fun settypeSafe(result: MethodChannel.Result) {
        val networkresult = Pigeon.NetworkResult()
        networkresult.apply {
            wifi = myIp.getdeviceIpAddress_Wifi()
            wifi_tether = myIp.getIpInterface(network_interfac.wlan1.name)
            wifiboth = myIp.getIPAddress_Util(true)
            myIp.getIpAddress_Private(object :
                MyIp.VolleyListner {
                override fun Onresponse(data: String) {

                    privates = data
                }
            })
            usb = myIp.getIpInterface(network_interfac.rndis0.name)
            cellular = myIp.getIpInterface(network_interfac.rmnet_data2.name)
            isWifiEnabled = connecteddevice.isWifiEnabled()
            isHotspotEnabled = connecteddevice.isHotspotEnabled()
            isWifiConnected = connecteddevice.isWifiConnected()
            wifiName = connecteddevice.getSsid()
            all_interface = myIp.getNetworkIp4LoopbackIps().toString()
            isLocationEnabled = if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == 0
            ) true else false


        }
//        println("====>" + networkresult.wifi_tether.toString())
        result.success(networkresult.toMap())

    }


    private fun setPermissions(result: MethodChannel.Result) {
        val accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(mactivity!!, accessFineLocation)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mactivity!!,
                arrayOf(accessFineLocation),
                PermissionManger.ACCESS_FINE_LOCATION_CODE
            );
        }
    }

    fun setActivity(activity: Activity) {
        mactivity = activity
    }

}