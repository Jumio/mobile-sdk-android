![ID Verification & Fastfill](images/id_verification.jpg)

# Transition Guide for ID Verification & Fastfill SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.7.3
#### Custom UI changes
*  The countryList parameter in [NetverifyCustomSDKInterface$onNetverifyCountriesReceived](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyCountriesReceived-java.util.Map-java.lang.String-) has been changed from `HashMap` to `Map`

#### ZoOm Customization Changes
* All the attributes starting with `face_` have been removed
* For a full guide please head over to the [FAQ](integration_faq.md#zoom-customization)

## 3.7.2
No backward incompatible changes

## 3.7.1
No backward incompatible changes

## 3.7.0

#### Dependency Changes
* Zoom update: ~~"com.facetec:zoom-authentication:8.0.11@aar"~~ is replaced by "com.facetec:zoom-authentication:8.12.1@aar"
* Room updated: ~~"androidx.room:room-runtime:2.2.3"~~ is replaced by "androidx.room:room-runtime:2.2.5"
* JMRTD updated: ~~"org.jmrtd:jmrtd:0.7.18"~~ is replaced by "org.jmrtd:jmrtd:0.7.19"
* Bouncycastle updated: ~~"org.bouncycastle:bcprov-jdk15on:1.64"~~ is replaced by "org.bouncycastle:bcprov-jdk15on:1.65"

#### API Changes
* `getEMRTDStatus()` has been removed from [NetverifyDocumentData](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDocumentData.html)
* New attributes for customization of the NFC help animation have been added to our `styles.xml`:
  * `netverify_nfc_passport_cover` - Outside passport cover color
  * `netverify_nfc_passport_page_dark` - Color of the last page inside the passport after opening
  * `netverify_nfc_passport_page_light` - Color of the flipped pages during opening
  * `netverify_nfc_passport_foreground` - Foreground color for the passport text and the phone screen
  * `netverify_nfc_phone_background` - Frame color for the phone that is positioned on the passport

* Attribute `netverify_nfc_dialog_theme` has been removed and is not required any more
* The position of the Jumio branding logo and privacy link changed to from bottom-right to center-top for all portrait scan views. In Custom UI, the top margin for this element can be adjusted using the following method [`setBrandingLogoTopMargin(int topMargin)`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanView.html#setBrandingLogoTopMargin-int-)

#### Customization Changes
* Android Zoom screens update. Details can be found in [Custom theme issues](known_issues.md#custom-theme-issues)

#### Localizable Strings
Several additions and changes in regards to the Android Zoom screens update.

__Added strings:__
* [`netverify_nfc_description_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L58-L65) strings
* [`zoom_action_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L107-L108) strings
* [`zoom_instructions_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L127-L128) strings
* [`zoom_result_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L129-L130) strings
* [`zoom_retry_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L131-L136) strings

__Removed strings:__
* `netverify_nfc_bac_dialog_xxx` strings
* `netverify_nfc_description_xxx` strings

## 3.6.2
The Proguard keep rule `-keep class com.jumio.** { *; }` has to be added to your Proguard rules, if it wasn't added yet.
Details can be found in chapter [Proguard](../README.md#proguard)

## 3.6.1
No backward incompatible changes

## 3.6.0
#### Dependency Changes
AndroidX Material design library has been *updated to version 1.1.0*
* ~~"com.google.android.material:material:1.0.0"~~ is replaced by "com.google.android.material:material:1.1.0"

Local broadcast manager dependency has been added mandatory due to the design library update where it was seperated by Google
* implementation "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"

JMRTD is now added as a gradle dependency - if you use NFC scanning please make sure to add the new dependencies:
* implementation "org.jmrtd:jmrtd:0.7.18"
* implementation "org.ejbca.cvc:cert-cvc:1.4.6"

## 3.5.0
#### Error Code Change
* Error code D (Wrong API credentials used, retry impossible) has been removed.

#### Proguard Change
* Consumer proguard rules have been added. All Jumio SDK proguard rules will now be applied automatically to the application when the Jumio Core library is included.

## 3.4.1
#### API Changes in NetverifyCustomScanInterface
* A new callback [onNetverifyUserConsentRequired](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyUserConsentRequired-java.lang.String-) was added to [NetverifyCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html)
#### API Changes in NetverifyCustomSDKController
* A new method [onUserConsented](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html#onUserConsented--) was added to [NetverifyCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html)

## 3.4.0
#### Customization Changes
* The SDK now uses Material buttons instead of the old snackbar button styles on the confirmation view.
* Please check out the confirmation screens and the XML output in the [Surface Tool](https://jumio.github.io/surface-android/) for all adapted content.
Changes:
* New style attribute **confirmationIcon** for customizing the info and warning icon on the confirmation screen.
* New confirmation button style **Netverify.Confirmation.MaterialButton** for the positive button.
* New confirmation button style **Netverify.Confirmation.MaterialButton.Outlined** for the negative button.

#### Dependency Changes
* Constraint layout dependency **androidx.constraintlayout:constraintlayout:1.1.3** or higher is now mandatory
* The following plugins are now required in the build.gradle of your application:
```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
```

* Dependency name and version change - com.facetec:zoom-authentication-hybrid:7.0.14 is replaced by **com.facetec:zoom-authentication:8.0.11@aar**
*  ~~androidx.appcompat:appcompat:1.0.2~~ is replaced by androidx.appcompat:appcompat:1.1.0
*  ~~androidx.room:room-runtime:2.0.0~~ is replaced by androidx.room:room-runtime:2.2.1

#### Custom UI Callbacks
*  ~~onNetverifyDisplayFlipDocumentHint~~ has been removed
*  A new parameter [NetverifyConfirmationType](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyConfirmationType.html) was added to [NetverifyCustomScanInterface$onNetverifyPresentConfirmationView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html#onNetverifyPresentConfirmationView-NetverifyConfirmationType-)

#### Proguard Changes
`-dontwarn com.facetec.zoom.sdk.**` needs to be added

## 3.3.2
No backward incompatible changes

## 3.3.1
No backward incompatible changes

## 3.3.0
#### Deallocation Callback
Added a new method [checkDeallocation](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#checkDeallocation-com.jumio.nv.NetverifyDeallocationCallback-) in the [NetverifySDK](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html) to check if the SDK resources have already been deallocated. The method requires a  [NetverifyDeallocationCallback](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDeallocationCallback.html) instance as a parameter and calls `onNetverifyDeallocated` once the SDK is deallocated. The checkDeallocation method should only be called once the SDK has returned a result and another SDK instance is required.

#### Dependency Change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.12~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.14

#### Date Changes
All dates are now UTC based. This affects the dates in [NetverifyDocumentData](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDocumentData.html) and [NetverifyCustomNfcAccess](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcAccess.html)

#### New SDK Localizations Added
SDK Translations for the languages Italian and Portuguese have been added.

## 3.2.1
#### Fixed a Face sSanning Problem in Which a Black Screen Was Shown to the User

## 3.2.0
#### Dependency Change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2
*  ~~com.facetec:zoom-authentication-hybrid:7.0.9~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.12

#### API Changes in NetverifyCustomScanInterface
A new parameter [NetverifyCancelReason](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCancelReason.html) was added to [NetverifyCustomScanInterface$onNetverifyScanForPartCanceled](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html#onNetverifyScanForPartCanceled-com.jumio.core.data.document.ScanSide-NetverifyCancelReason-)

#### Watchlist Screening
Two new methods for handling watchlist screening have been added. Please refer to the [Netverify guide](/docs/integration_bam-checkout.md#watchlist-screening)
```
netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED);
netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME");
```

#### Additional Functions in NetverifyCustomScanInterface
For the new NFC scanning functionality in custom ui, there has been some additions in [NetverifyCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html):
`getNetverifyCustomNfcInterface()` should return an instance of [NetverifyCustomNfcInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcInterface.html) or Null
`onNetverifyStartNfcExtraction(NetverifyCustomNfcPresenter)` is called when the NFC scanning can be started. The NFC scanning is controlled with the provided instance of [NetverifyCustomNfcPresenter](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcPresenter.html)

#### Customization
The following customization attribute have been added: `netverify_scanOverlayFill`

#### New Fastfill Offline Token Required
Due to a license model update in our Barcode implementation, it's necessary to regenerate the offline token when updating to version 3.2.0  

#### Close Button cCustomization in Custom UI
The position and image of the close button for face scanning can now be customized. Please have a look at the [NetverifyCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanView.html)

#### Custom UI Help Animation Support
[NetverifyCustomScanPresenter$getHelpAnimation](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#getHelpAnimation-com.jumio.nv.custom.NetverifyCustomAnimationView-) has been added to get the specific help animation in case the scan part is canceled. An instance of  [NetverifyCustomAnimationView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomAnimationView.html) needs to be passed where the animation is rendered in.

## 3.1.0
#### Dependency Changes
*  ~~com.madgag.spongycastle:prov:1.58.0.0~~ is replaced by org.bouncycastle:bcprov-jdk15on:1.61
*  Proguard rules containing *org.spongycastle* have been replaced with *org.bouncycastle*
*  ~~com.facetec:zoom-authentication-hybrid:7.0.5~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.9

## 3.0.0
#### Renamed Dependency NV-Face to Face
The dependency `com.jumio.android:nv-face` was renamed to `com.jumio.android:face`, reflecting the internal restructuring of the dependencies that was necessary for adding the new product Authentication

#### Renamed Public API Methods and Parameters
The following methods and the related parameters have been renamed to ensure consistency across all platforms
* `setRequireFaceMatch(..)` -> `setEnableIdentityVerification(..)`
* `setRequireVerification(..)` -> `setEnableVerification(..)`
* `setMerchantReportingCriteria(..)` -> `setReportingCriteria(..)`
* `setMerchantIdScanReference(..)` -> `setCustomerInternalReference(..)`
* `setCustomerId(..)` -> `setUserReference(..)`

#### Additional Property in NetverifyCustomScanView
`setMode(..)` must be called before the view is used. Possible values: NetverifyCustomScanView.MODE_ID or NetverifyCustomScanView.MODE_FACE


## 2.15.0
#### Added Room
Dependencies that have been added to the SDK:
+ androidx.room:room-runtime:2.0.0

#### Added 3D Liveness
###### Dependency Changes
* com.jumio.android:nv-face:2.15.0@aar
* com.facetec:zoom-authentication-hybrid:7.0.2@aar
* ~~com.jumio.android:nv-liveness:2.14.0@aar~~ The old liveness module is not supported anymore

The Facetec Maven repository also needs to be added: maven { url 'http://maven.facetec.com' }

###### Localization
Please have a look at the [strings-jumio-sdk.xml](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml) for all the new added strings prefixed with `zoom_`.
The following strings have been removed: `netverify_scanview_liveness_follow_hint`, `netverify_scanview_liveness_move_closer`, `netverify_scanview_liveness_move_back`, `netverify_scanview_liveness_description`, `netverify_helpview_full_description_liveness_glasses`, `netverify_helpview_full_description_liveness_cap` and `netverify_helpview_full_description_liveness_light`.

###### Customization
The following customization attributes have been added: `netverify_scanOverlayFaceBackground`, `netverify_scanOverlayFaceFeedbackText`, `netverify_scanOverlayFaceFeedbackBackground`, `netverify_scanOverlayFaceProgress`, `netverify_scanOverlayFaceOval`
The following customization attributes have been removed: `netverify_scanOverlayLivenessValid`, `netverify_scanOverlayLivenessInvalid`, `netverify_scanOverlayLivenessBackground`, `netverify_scanOverlayLivenessText`

###### Proguard
The following lines need to be added in your `proguard-rules.pro` file for 3D Liveness:
```
-keep class com.facetec.zoom.** { *; }
-dontwarn javax.annotation.Nullable
```

## 2.14.0
#### Migrate to AndroidX
The support library was migrated to [`AndroidX`](https://developer.android.com/jetpack/androidx/). As the developer page outlines, this is a mandatory step since all new Support Library development and maintenance will occur in the AndroidX library. This [`migration guide`](https://developer.android.com/jetpack/androidx/migrate) shows you how to migrate your application to AndroidX.

Check out the changed dependencies in the  [`dependencies section`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_id-verification-fastfill.md#dependencies) or in the [`build.gradle`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of the sample application.
The mapping for all support libraries is listed in section "Artifact mappings" [here](https://developer.android.com/jetpack/androidx/migrate)

Dependencies that changed in the SDK:
+ com.android.support:appcompat-v7:27.1.1 -> androidx.appcompat:appcompat:1.0.0
+ com.android.support:cardview-v7:27.1.1 -> androidx.cardview:cardview:1.0.0
+ com.android.support:design:27.1.1 -> com.google.android.material:material:1.0.0
- com.android.support:support-v4:27.1.1 -> androidx.legacy:legacy-support-v4:1.0.0 (was merged by AndroidX and can be therefore be fully removed)

#### Default Settings
The default values for [`requireVerification`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#setRequireVerification-boolean-) and [`requireFaceMatch`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#setRequireFaceMatch-boolean-) were changed to `true`. Please make sure that they are explicitly set to false in case a scan in Fastfill mode should be performed.

## 2.13.0
#### API Change in NetverifyDocumentData
The function `getMiddleName()` has been removed. If a middle name is available, it will be concatinated with the first name.

#### Removed Deprecated ABIs mips, mips64 and armeabi
These ABIs were deprecated in recent NDK toolsets as mentioned here - https://developer.android.com/ndk/guides/abis and are not used any more.

## 2.12.1
#### Fixed a Problem in Which the User Could Get Stuck in the Selfie Capturing Process

## 2.12.0
#### Fallback for Google Vision Not Operational Added
A fallback to manual image picker will now be used if Google Vision is not operational due to problems on the device. This guarantees the face workflow to be finished despite problems with the availablility of the Google Play services. Details are also described in [sub-chapter operationality](integration_id-verification-fastfill.md#operationality).
The method `netverifySDK.isMobileVisionOperational` remains in the SDK but doesn't need to be checked now necessarily.

#### New SDK Localizations Added
In addition to English, the translations for the languages Chinese (Simplified), Dutch, Frensh, German and Spanish have been added.

#### Remove Unused Strings for Localization
Along with the additional languages, we removed some Strings that were unused in the SDK. The following keys have been removed: `netverify_confirmation_snackbar_help_default`, `netverify_accessibility_select_your_country`, `netverify_accessibility_action_double_click`, `netverify_accessibility_select_your_country`, `netverify_scan_options_country_title`, `netverify_overlay_liveness_advice` and `netverify_scan_options_preselected_hint`.

#### Additional Information Method Removed
SDK method `netverifySDK.setAdditionalInformation` has been removed.

#### New Callback in NetverifyCustomScanInterface
`onNetverifyDisplayBlurHint()` was added for custom scan view.

## 2.11.0
#### New Error Scheme
The schema for `errorCode` changed and it's type is now String instead of Integer.
Read more detailed information on this in chapter [Error codes](/docs/integration_id-verification-fastfill.md#error-codes)

## 2.10.1
No backward incompatible changes.

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)

## 2.9.0

#### Changes in SDK Code
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
The usage is explained in the Netverify guide [sub-chapter operationality](integration_id-verification-fastfill.md#operationality)

#### Changes in Localizable Strings
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

#### Changes in SDK API
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Add NetverifySDK method `isRooted(Context)` for device root-check before starting the SDK

#### Changes in Localizable Strings
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

#### Provide Possibility to Avoid Loading Spinner After SDK Start
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
#### Removed Name Match Feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
#### Changes in Customization
Additions for the customization options to support the configuration of all scan overlays.


## Copyright

&copy; Jumio Corp. 395 Page Mill Road, Palo Alto, CA 94306
