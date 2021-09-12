import 'dart:async';

import 'package:flutter/services.dart';

class AndroidIp {
  static const MethodChannel _channel = const MethodChannel('android_ip');
  static const EventChannel networklistner_channel =
      const EventChannel('networklistner');

  static const EventChannel echannellist_channel =
      const EventChannel('echannellist');
  Stream<String>? _onConnectivityChanged;
  Stream<String>? _onDeviceConnected;

  Stream<String>? get onConnectivityChanged {
    _onConnectivityChanged = networklistner_channel
        .receiveBroadcastStream()
        .map((event) => event.toString());
    return _onConnectivityChanged;
  }

  Stream<String>? get onDeviceConnected {
    _onDeviceConnected = echannellist_channel
        .receiveBroadcastStream()
        .map((event) => event.toString());
    return _onDeviceConnected;
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool?> get shareself async {
    final bool? version = await _channel.invokeMethod('shareself');
    return version;
  }

  static Future<String?> get IpAddress_Wifi_tether async {
    final String? version = await _channel.invokeMethod('Wifi_tether');
    return version;
  }

  static Future<String?> get IpAddress_Wifi_tetherorwifi async {
    final String? version = await _channel.invokeMethod('Wifi_tetherorWifi');
    return version;
  }

  static Future<String?> get IpAddress_Private async {
    String? version = await getIp("Private");
    return version;
  }

  static Future<String?> get IpAddress_USB_tether async {
    String? version = await getIp("USB_tether");
    return version;
  }

  static Future<String?> get IpAddress_Wifi async {
    String? version = await getIp("Wifi");
    return version;
  }

  static Future<String?> get IpAddress_Blue_ther async {
    String? version = await getIp("Blue_ther");
    return version;
  }

  static Future<String?> get IpAddress_Cellular1 async {
    String? version = await getIp("Cellular1");
    return version;
  }

  static Future<String?> get IpAddress_Cellular2 async {
    String? version = await getIp("Cellular2");
    return version;
  }

  static Future<String?> get IpAddress_All async {
    String? version = await getIp("All");
    return version;
  }

  static Future<String?> get getConnectedList async {
    String? version = await getIp("ConnectedList");
    return version;
  }

  static Future<String?> getIp(String s) async {
    final String? version = await _channel.invokeMethod(s);
    return version;
  }
}
