# Known Issues

## Table of Contents
- [SDK Version 3.9.0](#sdk-version-390)
  - [Custom UI](#custom-ui)
- [SDK Version 3.8.0 and Newer](#sdk-version-380-and-newer)
  - [Stuck on 'Processing Documents' Screen ](#stuck-on-'processing-documents'-screen)
  - [Issues with okhttp3 Dependency Using iProov](#Issues-with-okhttp3-dependency-using-iproov)
  - [Jetifier Issues](#jetifier-issues)
  - [Fallback to Manual Capturing Using iProov](#fallback-to-manual-capturing-using-iProov)
  - [Custom UI](#custom-ui-1)
- [SDK Version 3.7.x](#sdk-version-37x)
  - [Kotlin Integration](#kotlin-integration)
- [Miscellaneous](#miscellaneous)
  - [Static Interface Methods Are only Supported with Android N](#Static-interface-methods-are-only-supported-with-Android-N)
  - [SDK Crashes Trying to Display Animations (Android Version 5 and Lower)](#sdk-crashes-trying-to-display-animations-(android-version-4-and-lower))
  - [Country Missing from the Country List](#country-missing-from-the-country-list)

# SDK Version 3.9.0

## Custom UI
* Make sure to display the `NetverifyCustomScanView` only AFTER calling `startScan()` as done in our [Sample](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/kotlin/netverify/customui/NetverifyCustomScanFragment.kt), to ensure that the scan presenter is fully initialized and the camera callback `onNetverifyCameraAvailable()` will be fired.

# SDK Version 3.8.0 and Newer

## Stuck on 'Processing Documents' Screen
If user is getting stuck indefinitely after scanning process during 'Processing documents' stage without a callback or error message, please make sure the following dependencies
```
classpath "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version  

apply plugin: 'kotlinx-serialization'

dependencies {  
  ...
  implementation "org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0"
  implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0"
  ...
}
```
have been included in your `build.gradle` file. Missing Kotlin serialization will result in infinite loading without a callback.

Please also refer to the 3.8.0 __"Dependency Changes"__ section of our [transition guide](transition-guide_id-verification-fastfill.md) and the `build.gradle`(https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of our sample application for additional information.

## Fallback to Manual Capturing Using iProov
In some rare cases, the iproov Token Call can take too long to complete, which means the token is not yet available when the decision for the Liveness Vendor is made. When this happens, the scan mode `FACE_MANUAL` (Manual Capturing) will be used as a fallback. This also applies to __Tablets__ and devices running on __Android Version 5__ ("Lollipop") or lower.

__Note:__ This is most likely to happen for applications implementing CustomUI that start with face scanning directly.

## Issues with okhttp3 Dependency Using iProov
iProov indirectly depends on okhttp 3.8.1. As of now, issues related to okhttp have been due to the use of okhttp 4.x.

This can be solved by downgrading the dependency that provides 4.x, by excluding the okhttp package from that dependency, or by a combination of both of these. (Please note that a 3.x version is not necessarily older than 4.x one.)

Please also refer to [iProov FAQ](https://github.com/iProov/android/wiki/Frequently-Asked-Questions#issues-with-okhttp3-dependency) for further information.

## Jetifier Issues
Due to a bug in the Jetifier, the Bouncycastle library needs to be added to the Jetifiers ignorelist in the [`gradle.properties`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/gradle.properties)
```
android.jetifier.blacklist=bcprov-jdk15on
```
Please note that the naming of this will change with the Android Gradle Plugin 4 release and will become `android.jetifier.ignorelist`

## Custom UI
On using iProov in CustomUI, in case `NetverifySDKController$retry` function is called for an error EX0000, the SDK can fail with the following exception: `java.lang.NoClassDefFoundError: Failed resolution of: Lcom/jumio/zoom/custom/ZoomCustomScanPresenter`. This issue was fixed in SDK version 3.9.0. As a workaround it is possible to only add `implementation "com.jumio.android:zoom:3.8.0@aar"` to the build.gradle dependencies.

# SDK Version 3.7.x

## Kotlin Integration  
Since the Jumio SDK is partly written in Kotlin, it is necessary to add the Kotlin standard library dependency to your project, even if the project itself if written in Java.

# Miscellaneous

## Static Interface Methods Are only Supported with Android N
Error message "Static interface methods are only supported with Android N" will be displayed when Java 8 compatibility is not enabled. In this case, please make sure to enable compatibility in your `build.gradle` file:
```
android {
...
  compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

## SDK Crashes Trying to Display Animations (Android Version 5 and Lower)
Running the SDK on API Level 21/Android Version 5 ("Lollipop") or lower, the application might crash when trying to display Jumio animations. In this case it is necessary to add the line `AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)` to the `onCreate()` method of your application or `Activity`, ideally before `setContentView()` is called.

__Note:__ Refer to [required for vector drawable compat handling](https://stackoverflow.com/a/37864531/1297835) for further information.

## Country Missing from the Country List
Countries with documents that are [MRZ](integration_glossary.md)-capable (which is the case for most passports) might not be available if the necessary MRZ dependency is missing. The dependency in question is:
```
implementation "com.jumio.android:nv-mrz:3.9.0@aar"
```
Countries with documents that have a barcode might not be available if the necessary MRZ dependency is missing. The dependency in question is:
```
implementation "com.jumio.android:nv-barcode:3.9.0@aar"
```
A complete list of all dependencies [can be found here.](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_id-verification-fastfill.md#dependencies)

__Note:__ Version numbers may vary.
