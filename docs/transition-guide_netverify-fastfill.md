![Fastfill & Netverify](images/netverify.png)

# Transition guide for Netverify & Fastfill SDK

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)

## 2.9.0

#### Changes in SDK code
* New cardview dependency was added `com.android.support:cardview-v7:26.1.0` for the screen redesign. This dependency is mandatory for Netverify
* Multidex is now mandatory, follow the steps Android Developers guide https://developer.android.com/studio/build/multidex.html#mdex-gradle to enable it if necessary in your app.
* Additional Proguard rules for the updated Barcode Scanner have to be added:
```
-keep class com.microblink.** { *; }
-keep class com.microblink.**$* { *; }
-dontwarn com.microblink.**
```
* SDK method for checking the Google Mobile Vision API operationality was added (see method documentation in [NetverifyFragment](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/NetverifyFragment.java) in the Sample app))
```
GoogleVisionStatus NetverifySDK.isMobileVisionOperational(Activity activity, int requestCode);
```
The usage is explained in the Netverify guide [sub-chapter operationality](integration_netverify-fastfill.md#operationality)

#### Changes in localizable strings
Multiple additions and changes in regards to the new selection screen.

#### Changes in Customization
Additions and changes in regards to the new selection screen (see XML output in [Surface Tool](https://jumio.github.io/surface-android/)).
Attributes added for replacing the previous selection screen: `netverify_scanOptionsItemHeaderBackground`, `netverify_scanOptionsItemForeground` and `netverify_scanOptionsItemBackground`.

## 2.8.0
* Dependency `com.jumio.android:nv-liveness:2.8.0@aar` is mandatory now.

## 2.7.0
* New Dependency `com.jumio.android:nv-liveness:2.7.0@aar` was added for face-liveness functionality.
* Dependency `com.google.android.gms:play-services-vision` is now mandatory required because of added functionality.
* Change SDK method `setEnableEpassport(boolean)` to `setEnableEMRTD(boolean)` beacause of to the support for NFC ID documents.
* If the dependencies `com.jumio.android:nv-liveness` and `com.jumio.android:nv-barcode-vision` are both used in the application, the following lines have to be added to the application tag in the AndroidManifest.xml to avoid merge issues.
```
<meta-data
			android:name="com.google.android.gms.vision.DEPENDENCIES"
			android:value="barcode, face"
			tools:replace="android:value"/>
```

* Additional Proguard rules for the Samsung Camera SDK have to be added:
```
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**
```

## 2.6.1
No backward incompatible changes.

## 2.6.0

#### Changes in SDK Api
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Add NetverifySDK method `isRooted(Context)` for device root-check before starting the SDK

#### Changes in localizable strings
Multiple additions and changes in regards to the new guidance / help screen.

#### Changes in Customization
Additions and changes in regards to the new guidance / help screen.

## 2.5.0
No backward incompatible changes.

## 2.4.0

#### Remove okHttp
The build.gradle was adapted to support standard UrlConnection for replacing okHttp

#### Changes in Customization
Override the theme that is used for Netverify in the manifest by calling `netverifySDK.setCustomTheme(CUSTOMTHEMEID)`. Use the resource id of a customized theme that uses `Theme.Netverify` as parent.
Additions and changes for customization options for the launch of the surface tool.

#### Provide possibility to avoid loading spinner after SDK start
Use the following method to initialize the SDK before displaying it
```
netverifySDK.initiate(new NetverifyInitiateCallback() {
 @Override
 public void onNetverifyInitiateSuccess() {
 }
 @Override
 public void onNetverifyInitiateError(int errorCode, int errorDetail, String errorMessage, boolean retryPossible) {
 }
});
 ```
#### Removed name match feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
#### Changes in Customization
Additions for the customization options to support the configuration of all scan overlays.


## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
