package com.shersoft.android_ip


import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.NonNull
import com.shersoft.android_ip.util.ConnectedDevice
import com.shersoft.android_ip.util.EnableDevice
import com.shersoft.android_ip.util.MyIp
import com.shersoft.android_ip.util.ShareFile
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


/** AndroidIpPlugin */
class AndroidIpPlugin : FlutterPlugin, ActivityAware {


    interface mybroadcastList {
        fun onChange(typeevent: String)
    }

    interface IDeviceConnected {
        fun DeviceConnected(ip: String)
    }

    interface IShare {
        fun onFileShared(status: String)
    }

    interface IPermissions {
        fun onSendPermission(name: String, status: Boolean)
        fun onError(name: String, status: Boolean, errorCode: String?)
    }

    interface OpenAppSettingsSuccessCallback {
        fun onSuccess(appSettingsOpenedSuccessfully: Boolean)
    }

    interface ErrorCallback {
        fun onError(errorCode: String?, errorDescription: String?)
    }

    lateinit var myIp: MyIp
    lateinit var iDeviceConnected: IDeviceConnected
    lateinit var connecteddevice: ConnectedDevice
    lateinit var menableDevice: EnableDevice
    lateinit var sharefile: ShareFile
    lateinit var context: Context
    override fun onDetachedFromActivity() {
        if (permissionmanger != null) {
            mbinding.addRequestPermissionsResultListener(permissionmanger)
            mbinding.addActivityResultListener(permissionmanger)
        }
        methodCallHandler = null
        mPermissionlistnerImp?.permissionmanger = null;
    }


    private lateinit var channel: MethodChannel
    private lateinit var echannel: EventChannel
    private lateinit var echannellist: EventChannel
    private lateinit var Permissionchannel: EventChannel
    private lateinit var sharechannel: EventChannel
    private lateinit var connManager: ConnectivityManager
    var methodCallHandler: MethodCallImpl? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

        context = flutterPluginBinding.applicationContext

        myIp = MyIp(context)

        connecteddevice = ConnectedDevice(context)
        menableDevice = EnableDevice(context)
        sharefile = ShareFile(context)
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "android_ip")
        methodCallHandler = MethodCallImpl(
            context,
            connecteddevice,
            menableDevice,
            sharefile,
            myIp
        )


        echannel = EventChannel(flutterPluginBinding.binaryMessenger, "networklistner")
        echannellist = EventChannel(flutterPluginBinding.binaryMessenger, "echannellist")
        sharechannel = EventChannel(flutterPluginBinding.binaryMessenger, "sharelistner")
        Permissionchannel = EventChannel(flutterPluginBinding.binaryMessenger, "permissionlistner")
        mNetworkListnerImp = NetworkListnerImp(context)
        mPermissionlistnerImp = PermissionlistnerImp(context)
        mSharelistnerImp = SharelistnerImp(context, sharefile)
        mConnectedDevicelist = ConnectedDevicelistimp(context, connecteddevice, myIp)
        channel.setMethodCallHandler(methodCallHandler)
        setEventChannel()

    }

    var mNetworkListnerImp: NetworkListnerImp? = null
    var mPermissionlistnerImp: PermissionlistnerImp? = null
    var mSharelistnerImp: SharelistnerImp? = null
    var mConnectedDevicelist: ConnectedDevicelistimp? = null
    private fun setEventChannel() {


        Permissionchannel.setStreamHandler(mPermissionlistnerImp)



        echannel.setStreamHandler(mNetworkListnerImp)
        echannellist.setStreamHandler(mConnectedDevicelist)

        sharechannel.setStreamHandler(mSharelistnerImp)
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        echannel.setStreamHandler(null)
        echannellist.setStreamHandler(null)
        mNetworkListnerImp = null;

    }


    lateinit var mactivity: Activity
    lateinit var permissionmanger: PermissionManger
    lateinit var mbinding: ActivityPluginBinding
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mactivity = binding.activity
        mbinding = binding;
        methodCallHandler?.mactivity = mactivity
        permissionmanger = PermissionManger(mactivity)
        mPermissionlistnerImp?.permissionmanger = permissionmanger;
        if (permissionmanger != null) {
            binding.addRequestPermissionsResultListener(permissionmanger)
            binding.addActivityResultListener(permissionmanger)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        if (permissionmanger != null) {
            mbinding.addRequestPermissionsResultListener(permissionmanger)
            mbinding.addActivityResultListener(permissionmanger)
        }
        methodCallHandler = null
        mPermissionlistnerImp?.permissionmanger = null;

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        mactivity = binding.activity
        mbinding = binding;
        methodCallHandler?.mactivity = mactivity
        permissionmanger = PermissionManger(mactivity)
        if (permissionmanger != null) {
            binding.addRequestPermissionsResultListener(permissionmanger)
            binding.addActivityResultListener(permissionmanger)
        }
    }
}






