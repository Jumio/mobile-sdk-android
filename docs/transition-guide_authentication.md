![Authentication](images/authentication.jpg)

# Transition Guide for Authentication SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.9.1
No backward incompatible changes

## 3.9.0
#### Dependency Changes
* Room update: ~~"androidx.room:room-runtime:2.2.5"~~ is replaced by "androidx.room:room-runtime:2.2.6"

* AndroidX Kotlin Extension update: ~~`"androidx.core:core-ktx:1.3.1"`~~ is replaced by `"androidx.core:core-ktx:1.3.2"`

* LocalBroadcastManager removed: ~~`"androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"`~~

## 3.8.0
#### Dependency Changes
* Face library update: ~~`"com.jumio.android:face"`~~ is replaced with `"com.jumio.android:zoom:3.8.0@aar"` and `"com.facetec:zoom-authentication:8.12.1@aar"`

* AndroidX Appcompat update: ~~`"androidx.appcompat:appcompat:1.1.0"`~~ is replaced by `"androidx.appcompat:appcompat:1.2.0"`
* Google Material Library update: ~~`"com.google.android.material:material:1.1.0"`~~ is replaced by `"com.google.android.material:material:1.2.1"`

## 3.7.3
#### ZoOm Customization changes
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
#### API Changes in AuthenticationCustomScanInterface
* A new callback [onAuthenticationUserConsentRequired](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKInterface.html#onAuthenticationUserConsentRequired-java.lang.String-) was added to [AuthenticationCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html)

#### API Changes in AuthenticationCustomSDKController
* A new method [onUserConsented](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#onUserConsented--) was added to [AuthenticationCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html)

## 3.4.0
#### Dependency Change
*  ~~androidx.appcompat:appcompat:1.0.2~~ is replaced by androidx.appcompat:appcompat:1.1.0
*  ~~androidx.room:room-runtime:2.0.0~~ is replaced by androidx.room:room-runtime:2.2.1
* Dependency name and version change - com.facetec:zoom-authentication-hybrid:7.0.14 is replaced by com.facetec:zoom-authentication:8.0.11@aar

#### Proguard Changes
`-dontwarn com.facetec.zoom.sdk.**` needs to be added

## 3.3.2
No backward incompatible changes

## 3.3.1
No backward incompatible changes

## 3.3.0
#### Deallocation Callback
Added a new method [checkDeallocation](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#checkDeallocation-com.jumio.auth.AuthenticationDeallocationCallback-) in the [AuthenticationSDK](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html) to check if the SDK resources have already been deallocated. The method requires a  [AuthenticationDeallocationCallback](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationDeallocationCallback.html) instance as a parameter and calls `onAuthenticationDeallocated` once the SDK is deallocated. The checkDeallocation method should only be called once the SDK has returned a result and another SDK instance is required.

#### Dependency Change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.12~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.14

#### Change in Initiate Method
The enrollmentTransactionReference parameter has been moved to its own [setter](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setEnrollmentTransactionReference-java.lang.String-). It needs to be called before the initiate method is called.
In case an Authentication transaction has been created via the facemap server to server API [setAuthenticationTransactionReference](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setAuthenticationTransactionReference-java.lang.String-) should be used. Therefore [setEnrollmentTransactionReference](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setEnrollmentTransactionReference-java.lang.String-) should not be called.

#### New SDK Localizations Added
SDK Translations for the languages Italian and Portuguese have been added.

## 3.2.1
#### Fixed a Face Scanning Problem in Which a Black Screen Was Shown to the User

## 3.2.0
#### Dependency Changes
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2
*  ~~com.facetec:zoom-authentication-hybrid:7.0.9~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.12

#### API Changes in AuthenticationCustomScanInterface
A new parameter [AuthenticationCancelReason](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCancelReason.html) was added to [AuthenticationCustomScanInterface$onAuthenticationScanCanceled](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html#onAuthenticationScanCanceled-com.jumio.auth.custom.AuthenticationCancelReason-)

#### Close Button Customization in Custom UI
The position and image of the close button for can now be customized. Please have a look at the [AuthenticationCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanView.html)

#### Custom UI Help Animation Support
[AuthenticationCustomSDKController$getHelpAnimation](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#getHelpAnimation-com.jumio.auth.custom.AuthenticationCustomAnimationView-) has been added to get the specific help animation in case the scan part is canceled. An instance of  [AuthenticationCustomAnimationView](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomAnimationView.html) needs to be passed where the animation is rendered in.

## 3.1.0
#### Dependency Change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.5~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.9

## 3.0.0
Introduction of the Authentication product

## Copyright
&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
