#import "AndroidIpPlugin.h"
#if __has_include(<android_ip/android_ip-Swift.h>)
#import <android_ip/android_ip-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "android_ip-Swift.h"
#endif

@implementation AndroidIpPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidIpPlugin registerWithRegistrar:registrar];
}
@end
