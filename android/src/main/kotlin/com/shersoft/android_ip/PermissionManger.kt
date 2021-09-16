package com.shersoft.android_ip

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager

class PermissionManger(val activity: Activity) :
    io.flutter.plugin.common.PluginRegistry.ActivityResultListener,
    io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
        }
        return false;
    }

    companion object {
        val ACCESS_FINE_LOCATION_CODE = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ): Boolean {
        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults?.isNotEmpty() == true && grantResults?.get(0) ?: null == PackageManager.PERMISSION_GRANTED) {
//                if (::iPermissions.isLateinit)
                iPermissions.let {
                    it?.onSendPermission(
                        "ACCESS_FINE_LOCATION_CODE",
                        true
                    )
                }


            } else {

                iPermissions.let {
                    it?.onSendPermission(
                        "ACCESS_FINE_LOCATION_CODE".toString(),
                        false
                    )
                }

            }
        }
        return true;
    }

    var iPermissions: AndroidIpPlugin.IPermissions? = null;
    fun setListner(iPermissions: AndroidIpPlugin.IPermissions) {
        this.iPermissions = iPermissions;
    }
}
