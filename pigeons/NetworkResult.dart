// flutter pub run pigeon --i nput pigeons\NetworkResult.dart --dart_out lib\pigeon.dart  --java_out android/src/main/kotlin/com/shersoft/android_ip/Pigeon.java --java_package "com .shersoft.android_ip"

// // // // import 'package:pigeon/pigeon_lib.dart';
// // //
class NetworkResult {
  String? wifi;
  String? wifi_tether;
  String? wifiboth;
  String? privates;
  String? cellular;
  String? Usb;
  String? Bluethooth;
  String? WifiName;
  String? all_interface;
  bool? IsWifiConnected;
  bool? IsHotspotEnabled;
  bool? IsWifiEnabled;
  bool? IsLocationEnabled;
}

@HostApi()
abstract class Api2Host {
  @async
  NetworkResult getResult(NetworkResult results);

  @async
  Hotspot getHotspot(Hotspot results);
}

@HostApi()
abstract class PermissionResult2Host {
  @async
  PermissionResult getResult(PermissionResult results);
}

class PermissionResult {
  String? name;
  bool? status;
}

class Hotspot {
  String? name;
  String? presharedkey;
  bool? enabled;
}
