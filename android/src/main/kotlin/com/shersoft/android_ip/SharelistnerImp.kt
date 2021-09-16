package com.shersoft.android_ip

import android.content.Context
import com.shersoft.android_ip.util.ShareFile
import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SharelistnerImp(val context: Context, val sharefile: ShareFile) : EventChannel.StreamHandler {

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {


        sharefile.shareAppAsAPK(context, object : AndroidIpPlugin.IShare {
            override fun onFileShared(status: String) {
                scope.launch {
                    events?.success(status)
                }

            }

        })

    }

    override fun onCancel(arguments: Any?) {
        println(arguments.toString())
    }

}
