import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:android_ip/android_ip.dart';

void main() {
  const MethodChannel channel = MethodChannel('android_ip');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AndroidIp.platformVersion, '42');
  });
}
