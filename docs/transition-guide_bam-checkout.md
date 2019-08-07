![BAM Checkout](images/bam_checkout.png)

# Transition guide for BAM Checkout SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.3.0
No backward incompatible changes

## 3.2.1
No backward incompatible changes

## 3.2.0
#### Dependency change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2

## 3.1.0
No backward incompatible changes

## 3.0.0
No backward incompatible changes.

## 2.15.0
#### Added Room
Dependencies that have been added to the SDK:
+ androidx.room:room-runtime:2.0.0  

## 2.14.0
#### Migrate to AndroidX
The support library was migrated to [`AndroidX`](https://developer.android.com/jetpack/androidx/). As the developer page outlines, this is a mandatory step since all new Support Library development and maintenance will occur in the AndroidX library. This [`migration guide`](https://developer.android.com/jetpack/androidx/migrate) shows you how to migrate your application to AndroidX.

Check out the changed dependencies in the  [`dependencies section`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_netverify-fastfill.md#dependencies) or in the [`build.gradle`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of the sample application.
The mapping for all support libraries is listed in section "Artifact mappings" [here](https://developer.android.com/jetpack/androidx/migrate)

Dependencies that changed in the SDK:
+ com.android.support:appcompat-v7:27.1.1 -> androidx.appcompat:appcompat:1.0.0
+ com.android.support:design:27.1.1 -> com.google.android.material:material:1.0.0
- com.android.support:support-v4:27.1.1 -> androidx.legacy:legacy-support-v4:1.0.0 (was merged by AndroidX and can be therefore be fully removed)

## 2.13.0
#### Removed deprecated ABIs mips, mips64 and armeabi
These ABIs were deprecated in recent NDK toolsets as mentioned here - https://developer.android.com/ndk/guides/abis and are not used any more.

## 2.12.1
No backward incompatible changes.

## 2.12.0
No backward incompatible changes.

## 2.11.0
#### New error scheme
The schema for `errorCode` changed and it's type is now String instead of Integer.
Read more detailed information on this in chapter [Error codes](/docs/integration_bam-checkout.md#error-codes)

## 2.10.1
No backward incompatible changes.

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)
* Removed support for Starbucks cards

## 2.9.0
No backward incompatible changes.

## 2.8.0
No backward incompatible changes.

## 2.7.0
* Removed SDK method `setAdyenPublicKey(String)` along removing all adyen functionality.
* Additional Proguard rules for the Samsung Camera SDK have to be added:
```
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**
```

## 2.6.1
No backward incompatible changes.

## 2.6.0
No backward incompatible changes.

## 2.5.0

#### BAM Checkout SDK offline with an offline token
Added the option to create an SDK instance for offline scanning with `BamSDK.create(rootActivity, YOUROFFLINETOKEN)`

## 2.4.0
#### Removed name match feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
No backward incompatible changes.

## 2.2.0
#### Changes in Customization
Multiple additions and changes in regards to a new user interface design to BAM Checkout Mobile


## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
