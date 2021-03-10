![Jumio](images/document_verification.jpg)

# Transition Guide for Document Verification SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.9.1
No backward incompatible changes

## 3.9.0
#### Dependency Changes
* Room update: ~~"androidx.room:room-runtime:2.2.5"~~ is replaced by "androidx.room:room-runtime:2.2.6"

* LocalBroadcastManager removed: ~~`"androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"`~~

## 3.8.0
#### Dependency Changes
* AndroidX Appcompat update: ~~`"androidx.appcompat:appcompat:1.1.0"`~~ is replaced by `"androidx.appcompat:appcompat:1.2.0"`
* Google Material Library update: ~~`"com.google.android.material:material:1.1.0"`~~ is replaced by `"com.google.android.material:material:1.2.1"`

## 3.7.3
No backward incompatible changes

## 3.7.2
No backward incompatible changes

## 3.7.1
No backward incompatible changes

## 3.7.0
#### Dependency Changes
* Room updated: ~~"androidx.room:room-runtime:2.2.3"~~ is replaced by "androidx.room:room-runtime:2.2.5"

## 3.6.2
The Proguard keep rule `-keep class com.jumio.** { *; }` has to be added to your Proguard rules, if it wasn't added yet.
Details can be found in chapter [Proguard](../README.md#proguard)

## 3.6.1
No backward incompatible changes

## 3.6.0
#### Dependency Changes
AndroidX Material design library has been *updated to version 1.1.0*
* ~~"com.google.android.material:material:1.0.0"~~ is replaced by "com.google.android.material:material:1.1.0"

Local broadcast manager dependency has been added mandatory due to the design library update where it was separated by Google
* implementation "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"

## 3.5.0
#### Error Code Change
Error code D (Wrong API credentials used, retry impossible) has been removed

#### Proguard Change
* Consumer Proguard rules have been added. All Jumio SDK Proguard rules will now be applied automatically to the application when the Jumio Core library is included.

## 3.4.1
No backward incompatible changes

## 3.4.0
#### Dependency Changes
*  ~~androidx.appcompat:appcompat:1.0.2~~ is replaced by androidx.appcompat:appcompat:1.1.0
*  ~~androidx.room:room-runtime:2.0.0~~ is replaced by androidx.room:room-runtime:2.2.1

## 3.3.2
No backward incompatible changes

## 3.3.1
No backward incompatible changes

## 3.3.0
#### New SDK Localizations Added
SDK Translations for the languages Italian and Portuguese have been added.


## 3.2.1
No backward incompatible changes

## 3.2.0
#### Dependency Change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2

## 3.1.0
No backward incompatible changes

## 3.0.0
#### Renamed Public API Methods and Parameters
The following methods and the related parameters have been renamed to ensure consistency across all platforms
* `setMerchantReportingCriteria(..)` -> `setReportingCriteria(..)`
* `setMerchantIdScanReference(..)` -> `setCustomerInternalReference(..)`
* `setCustomerId(..)` -> `setUserReference(..)`

## 2.15.0
#### Added Room
Dependencies that have been added to the SDK:
+ androidx.room:room-runtime:2.0.0  

## 2.14.0
#### Migrate to AndroidX
The support library was migrated to [`AndroidX`](https://developer.android.com/jetpack/androidx/). As the developer page outlines, this is a mandatory step since all new Support Library development and maintenance will occur in the AndroidX library. This [`migration guide`](https://developer.android.com/jetpack/androidx/migrate) shows you how to migrate your application to AndroidX.

Check out the changed dependencies in the  [`dependencies section`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_id-verification-fastfill.md#dependencies) or in the [`build.gradle`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of the sample application.
The mapping for all support libraries is listed in section "Artifact mappings" [here](https://developer.android.com/jetpack/androidx/migrate)

Dependencies that changed in the SDK:
+ com.android.support:appcompat-v7:27.1.1 -> androidx.appcompat:appcompat:1.0.0
+ com.android.support:design:27.1.1 -> com.google.android.material:material:1.0.0
- com.android.support:support-v4:27.1.1 -> androidx.legacy:legacy-support-v4:1.0.0 (was merged by AndroidX and can be therefore be fully removed)

#### Removed Document Type USSS
The document type for US social security card (USSS) was removed from the SDK.

#### Default Settings
The default value for [`enableExtraction`](https://jumio.github.io/mobile-sdk-android/com/jumio/dv/DocumentVerificationSDK.html#setEnableExtraction-boolean-) was changed to `true`. Please make sure that it is explicitly set to false in case a scan without extraction should be performed.

## 2.13.0
#### Removed Deprecated ABIs mips, mips64 and armeabi
These ABIs were deprecated in recent NDK toolsets as mentioned here - https://developer.android.com/ndk/guides/abis and are not used any more.

#### New SDK Localizations Added
In addition to English, the translations for the languages Chinese (Simplified), Dutch, Frensh, German and Spanish have been added.

## 2.12.1
No backward incompatible changes.

## 2.12.0
#### New Method setEnableExtraction
SDK method `documentVerificationSDK.setEnableExtraction(boolean)` has been added for enabling/disabling extraction on documents.

#### Additional Information Method Removed
SDK method `documentVerificationSDK.setAdditionalInformation` has been removed.

## 2.11.0
#### New Error Scheme
The schema for `errorCode` changed and it's type is now String instead of Integer.
Read more detailed information on this in chapter [Error codes](/docs/integration_document-verification.md#error-codes)

## 2.10.1
No backward incompatible changes.

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)

## 2.9.0
No backward incompatible changes.

## 2.8.0
No backward incompatible changes.

## 2.7.0
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Additional Proguard rules for the Samsung Camera SDK have to be added:
 ```
 -keep class com.samsung.** { *; }
 -keep class com.samsung.**$* { *; }
 -dontwarn com.samsung.**
 ```

## 2.6.1
No backward incompatible changes.

## 2.6.0
#### Changes in SDK API
* Add DocumentVerificationSDK method `isRooted(Context)` for device root-check before starting the SDK

## 2.5.0
No backward incompatible changes.

## 2.4.0
No backward incompatible changes.

## 2.3.0
No backward incompatible changes.

## Copyright

&copy; Jumio Corp. 395 Page Mill Road, Palo Alto, CA 94306
