package com.shersoft.android_ip

import android.content.Context
import io.flutter.plugin.common.EventChannel

class PermissionlistnerImp(context: Context) : EventChannel.StreamHandler {
    var permissionmanger: PermissionManger? = null
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        permissionmanger?.setListner(object : AndroidIpPlugin.IPermissions {
            override fun onSendPermission(name: String, status: Boolean) {
                val permissionResult = Pigeon.PermissionResult()
                permissionResult.apply {
                    this.name = name;this.status = status
                }
                events?.success(permissionResult.toMap())
            }

            override fun onError(name: String, status: Boolean, errorCode: String?) {
                val permissionResult = Pigeon.PermissionResult()
                permissionResult.apply {
                    this.name = name;this.status = status
                }
                events?.success(permissionResult)
            }

        })
    }

    override fun onCancel(arguments: Any?) {
        println(arguments.toString())
    }
}
