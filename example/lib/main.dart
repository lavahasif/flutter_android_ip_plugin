import 'dart:async';

import 'package:android_ip/android_ip.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {

  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _Connecton_change = '';
  String _IpAddress_Wifi_tether = 'Unknown';
  String _IpAddress_Wifi = 'Unknown';
  String _IpAddress_Private = 'Unknown';
  String _IpAddress_USB_tether = 'Unknown';
  String _IpAddress_All = 'Unknown';
  String _IpAddress_Cellular2 = 'Unknown';
  String _IpAddress_Cellular1 = 'Unknown';
  String _IpAddress_Cell = 'Unknown';
  String _IpAddress_Blue_ther = 'Unknown';

  @override
  void initState() {
    super.initState();
    var listner = new AndroidIp().onConnectivityChanged;
    listner!.listen((event) {
      setState(() {
        _Connecton_change = event;
      });
      initPlatformState();
    });

    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String IpAddress_Wifi_tether;
    String IpAddress_Wifi;
    String IpAddress_Private;
    String IpAddress_USB_tether;
    String IpAddress_Cellular1;
    String IpAddress_Cellular2;
    String IpAddress_Blue_ther;
    String IpAddress_All;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      IpAddress_Wifi_tether =
          await AndroidIp.IpAddress_Wifi_tether ?? 'Unknown Number';
      IpAddress_Wifi = await AndroidIp.IpAddress_Wifi ?? 'Unknown Number';
      IpAddress_Private = await AndroidIp.IpAddress_Private ?? 'Unknown Number';
      IpAddress_USB_tether =
          await AndroidIp.IpAddress_USB_tether ?? 'Unknown Number';
      IpAddress_Cellular1 =
          await AndroidIp.IpAddress_Cellular1 ?? 'Unknown Number';
      IpAddress_Cellular2 =
          await AndroidIp.IpAddress_Cellular2 ?? 'Unknown Number';
      IpAddress_Blue_ther =
          await AndroidIp.IpAddress_Blue_ther ?? 'Unknown Number';
      IpAddress_All = await AndroidIp.IpAddress_All ?? 'Unknown Number';
    } on PlatformException {
      IpAddress_Wifi_tether = 'Failed to get ';
      IpAddress_Wifi = 'Failed to get ';
      IpAddress_Private = 'Failed to get ';
      IpAddress_USB_tether = 'Failed to get ';
      IpAddress_Cellular1 = 'Failed to get ';
      IpAddress_Cellular2 = 'Failed to get ';
      IpAddress_Blue_ther = 'Failed to get ';
      IpAddress_All = 'Failed to get ';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _IpAddress_Wifi_tether = IpAddress_Wifi_tether;
      _IpAddress_Wifi = IpAddress_Wifi;
      _IpAddress_Private = IpAddress_Private;
      _IpAddress_USB_tether = IpAddress_USB_tether;
      _IpAddress_Cellular1 = IpAddress_Cellular1;
      _IpAddress_Cellular2 = IpAddress_Cellular2;
      _IpAddress_Blue_ther = IpAddress_Blue_ther;
      _IpAddress_All = IpAddress_All;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text('Changes on: $_Connecton_change\n'),
            Row(
              children: [
                Text('Wifi on: $_IpAddress_Wifi\n'),       SizedBox(width: 25,),
                RaisedButton(color:Colors.blue,child:Text("Copy"),onPressed:()=> _copy(_IpAddress_Wifi))
              ],
            ),
            Row(
              children: [
                Text('Wifi_tether on: $_IpAddress_Wifi_tether\n'),       SizedBox(width: 25,),
                RaisedButton(color:Colors.blue,child:Text("Copy"),onPressed:()=> _copy(_IpAddress_Wifi_tether))
              ],
            ),
            Text('Private on: $_IpAddress_Private\n'),
            Row(
              children: [
                Text('Wifi_tether on: $_IpAddress_Cellular1\n'),
                SizedBox(width: 25,),
                RaisedButton(color:Colors.blue,child:Text("Copy"),onPressed:()=>  _copy(_IpAddress_Cellular1))
              ],
            ),
            Text('Cellular2 on: $_IpAddress_Cellular2\n'),
            Row(
              children: [
                Text('USB_tether on: $_IpAddress_USB_tether\n'),       SizedBox(width: 25,),
                RaisedButton(color:Colors.blue,child:Text("Copy"),onPressed:()=>  _copy(_IpAddress_USB_tether))
              ],
            ),
            Row(
              children: [
                Text('USB_tether on: $_IpAddress_Blue_ther\n'),
                RaisedButton(color:Colors.blue,child:Text("Copy"),onPressed:()=>  _copy(_IpAddress_Blue_ther))
              ],
            ),
            Text('All on: $_IpAddress_All\n'),
            RaisedButton(
              color: Colors.blue,
              child: Text("Share Me"),
              onPressed: () async {
                await AndroidIp.shareself;
              },
            ),
          ],
        ),
      ),
    );
  }

  _copy(String ipAddress_Wifi) {
    var clipboardData = ClipboardData(text: ipAddress_Wifi);

    Clipboard.setData(clipboardData);
  }
}
