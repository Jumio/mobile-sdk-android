![Jumio](images/document_verification.png)

# Transition guide for Document Verification SDK

## 2.12.1
No backward incompatible changes.

## 2.12.0
#### New method setEnableExtraction
SDK method `documentVerificationSDK.setEnableExtraction(boolean)` has been added for enabling/disabling extraction on documents.

#### Additional information method removed
SDK method `documentVerificationSDK.setAdditionalInformation` has been removed.

## 2.11.0
#### New error scheme
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
#### Changes in SDK Api
* Add DocumentVerificationSDK method `isRooted(Context)` for device root-check before starting the SDK

## 2.5.0
No backward incompatible changes.

## 2.4.0
No backward incompatible changes.

## 2.3.0
No backward incompatible changes.

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
