package com.shersoft.android_ip.util

import android.Manifest
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.shersoft.android_ip.AndroidIpPlugin
import kotlinx.coroutines.*
import java.io.*
import java.lang.reflect.Method
import java.net.InetAddress


open class ConnectedDevice(var contexts: Context) {
    init {
//        EnableDevice(contexts)
    }

    fun isHotspotEnabled(): Boolean {
//        val AP_STATE_DISABLING = 10
//        val AP_STATE_DISABLED = 11
//        val AP_STATE_ENABLING = 12
//        val AP_STATE_ENABLED = 13 hotspot on
//        val AP_STATE_FAILED = 14


        val wifiManager =
            getwifiManager()
        val method: Method = wifiManager.javaClass.getMethod(
            "getWifiApState"
        )
        method.isAccessible = true
        val invoke = method.invoke(wifiManager)
//        println(invoke)

        return invoke == 13;
    }

    fun isWifiEnabled(): Boolean {


        val wifiManager =
            getwifiManager()


        return wifiManager.isWifiEnabled;
    }

    private val LOCATION = 1
    fun getSsid(): String {
        if (ActivityCompat.checkSelfPermission(
                contexts,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //Request permission from user
//            ActivityCompat.requestPermissions(
//                contexts.,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION
//            )
        } else { //Permission already granted
            val wifiInfo = (contexts.getApplicationContext()
                .getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                val ssid = wifiInfo.ssid //Here you can access your SSID
                println(ssid)
            }
        }

        val wifiManager =
            getwifiManager()


        return wifiManager.connectionInfo.ssid;
    }

     fun getwifiManager(): WifiManager {
        val wifiManager =
            contexts.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        return wifiManager
    }



    fun isWifiConnected(): Boolean {

        val connManager = contexts.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?

        val wifi: NetworkInfo = connManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!
        return wifi.isConnected;
    }


    fun getARPIps(): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        try {
//        val args = listOf("ip", "neigh")
//        val cmd = ProcessBuilder(args)
//        val process: Process = cmd.start()
            val process = Runtime.getRuntime().exec("ip neigh")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine {
                if (!it.contains("FAILED")) {
                    val split = it.split("\\s+".toRegex())
                    if (split.size > 4 && split[0].matches(Regex("([0-9]{1,3}\\.){3}[0-9]{1,3}"))) {
                        result.add(Pair(split[0], split[4]))
                    }
                }
            }
            val errReader = BufferedReader(InputStreamReader(process.errorStream))
            errReader.forEachLine {
                Log.e("===>connected device", it)
                // post the error message to server
            }
            reader.close()
            errReader.close()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
            // post the error message to server
        }
        for (pair in result) {

            Log.e("===>connected device", pair.first + "" + pair.second)
        }
        return result
    }

    fun getClientList() {
        var macCount = 0
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                val splitted = line.split(" +").toTypedArray()
                if (splitted != null) {
                    // Basic sanity check
                    val mac = splitted[3]
                    println("Mac : Outside If $mac")
                    val regex = "..:..:..:..:..:.."
                    if (mac.matches(Regex(regex))) {
                        macCount++
                        /* ClientList.add("Client(" + macCount + ")");
                    IpAddr.add(splitted[0]);
                    HWAddr.add(splitted[3]);
                    Device.add(splitted[5]);*/println("Mac : " + mac + " IP Address : " + splitted[0])
                        println("Mac_Count  $macCount MAC_ADDRESS  $mac")
                        Log.e("===>connected device", "IP Address : " + splitted[0])
                        Toast.makeText(
                            contexts.getApplicationContext(),
                            "Mac_Count  " + macCount + "   MAC_ADDRESS  "
                                    + mac, Toast.LENGTH_SHORT
                        ).show()
                    }
                    /* for (int i = 0; i < splitted.length; i++)
                    System.out.println("Addressssssss     "+ splitted[i]);*/
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("===>connected device", e.toString())
        }
    }


    fun getClientIPByMac(mac: String? = "192.168.240.156"): String? {
        var res: String? = ""
        if (mac == null) return res
        val flushCmd = "sh ip -s -s neigh flush all"
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec(flushCmd, null, File("/proc/net"))
        } catch (e: java.lang.Exception) {
        }
        val br: BufferedReader
        try {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String
            while (br.readLine().also { line = it } != null) {
                val sp = line.split(" +").toTypedArray()
                if (sp.size >= 4 && mac == sp[3]) {
                    val ip = sp[0]
                    if (ip.matches(Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) && sp[2] == "0x2") {
                        res = ip
                        break
                    }
                }
            }
            br.close()
        } catch (e: java.lang.Exception) {
            println("==============>devices" + e)
        }
        println("==============>devices" + res)
        return res
    }

    fun getListOfConnectedDevice() {
        val thread = Thread {
            var br: BufferedReader? = null
            var isFirstLine = true
            try {
                br = BufferedReader(FileReader("/proc/net/arp"))
                var line: String
                while (br.readLine().also { line = it } != null) {
                    if (isFirstLine) {
                        isFirstLine = false
                        continue
                    }
                    val splitted = line.split(" +").toTypedArray()
                    if (splitted != null && splitted.size >= 4) {
                        val ipAddress = splitted[0]
                        val macAddress = splitted[3]
                        val isReachable: Boolean = InetAddress.getByName(
                            splitted[0]
                        )
                            .isReachable(500) // this is network call so we cant do that on UI thread, so i take background thread.
                        if (isReachable) {
                            Log.d(
                                "Device Information", ipAddress + " : "
                                        + macAddress
                            )
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                try {
                    br!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }


    fun executeCmd(cmd: String, sudo: Boolean): String? {
        try {
            val p: Process
            p = if (!sudo) Runtime.getRuntime().exec(cmd) else {
                Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            }
            val stdInput = BufferedReader(InputStreamReader(p.inputStream))
            var s: String
            var res = ""
            while (stdInput.readLine().also { s = it } != null) {
                res += """
                $s
                
                """.trimIndent()
            }
            p.destroy()
            println("==============>devices" + res)
            return res
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @Throws(IOException::class, InterruptedException::class)
    fun pingHost(host: String, timeout: Int): Int {
        var timeout = timeout
        val runtime = Runtime.getRuntime()
        timeout /= 1000
        val cmd = "ping -c 1 -W $timeout $host"
        val proc = runtime.exec(cmd)
//        Log.d("===========>", cmd)
        proc.waitFor()
        return proc.exitValue()
    }

    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.awaitAll()
    }

    fun gethostData(host: String, iDeviceConnected: AndroidIpPlugin.IDeviceConnected) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val split: MutableList<String> = host.split(".") as MutableList<String>

        val drop = split.removeAt(split.size - 1)
        var ip = ""
        split.forEach { ip += it + "." }
        scope.launch {
            var ipse = arrayOf(10, 20, 80, 156, 229, 250)
            val array1 = (0 until 90)
            val array2 = (91 until 180)
            val array3 = (181 until 255)

//            var arr = arrayOf(array1, array2, array3)
            var output = (0..255).pmap {


                var ips = ip + "$it";

                if (pingHost(ips, 1000) == 0) {
//                    println("Connection============>$ips")
                    iDeviceConnected.DeviceConnected(ips);
//                Log.i("Connection=======>", "$ips".toString())
                };

            }


//            for (i in 0..255) {
//                var ips = ip + "$i";
//
//                if (pingHost(ips, 1000) == 0) {
////                    println("Connection============>$ips")
//                    iDeviceConnected.DeviceConnected(ips);
////                Log.i("Connection=======>", "$ips".toString())
//                };
//            }
        }

    }

    fun openAppSettings(
        context: Context?,
        successCallback: AndroidIpPlugin.OpenAppSettingsSuccessCallback,
        errorCallback: AndroidIpPlugin.ErrorCallback
    ) {
        if (context == null) {
            Log.d("PermissionConstants", "Context cannot be null.")
            errorCallback.onError(
                "PermissionHandler.AppSettingsManager",
                "Android context cannot be null."
            )
            return
        }
        try {
            val settingsIntent = Intent()
            settingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
            settingsIntent.data = Uri.parse("package:" + context.packageName)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(settingsIntent)
            successCallback.onSuccess(true)
        } catch (ex: java.lang.Exception) {
            successCallback.onSuccess(false)
        }
    }

}

