![Authentication](images/authentication.jpg)

# Transition guide for Authentication SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.6.0
#### Dependency Changes
AndroidX Material design library has been *updated to version 1.1.0*
* ~~"com.google.android.material:material:1.0.0"~~ is replaced by "com.google.android.material:material:1.1.0"

Local broadcast manager dependency has been added mandatory due to the design library update where it was seperated by Google
* implementation "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"

## 3.5.0
#### Error code change
Error code D (Wrong API credentials used, retry impossible) has been removed

#### Proguard change
* Consumer proguard rules have been added. All Jumio SDK proguard rules will now be applied automatically to the application when the Jumio Core library is included.

## 3.4.1
#### API changes in AuthenticationCustomScanInterface
* A new callback [onAuthenticationUserConsentRequired](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKInterface.html#onAuthenticationUserConsentRequired-java.lang.String-) was added to [AuthenticationCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html)

#### API changes in AuthenticationCustomSDKController
* A new method [onUserConsented](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#onUserConsented--) was added to [AuthenticationCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html)

## 3.4.0
#### Dependency change
*  ~~androidx.appcompat:appcompat:1.0.2~~ is replaced by androidx.appcompat:appcompat:1.1.0
*  ~~androidx.room:room-runtime:2.0.0~~ is replaced by androidx.room:room-runtime:2.2.1
* Dependency name and version change - com.facetec:zoom-authentication-hybrid:7.0.14 is replaced by com.facetec:zoom-authentication:8.0.11@aar

#### Proguard changes
`-dontwarn com.facetec.zoom.sdk.**` needs to be added

## 3.3.2
No backward incompatible changes

## 3.3.1
No backward incompatible changes

## 3.3.0
#### Deallocation callback
Added a new method [checkDeallocation](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#checkDeallocation-com.jumio.auth.AuthenticationDeallocationCallback-) in the [AuthenticationSDK](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html) to check if the SDK resources have already been deallocated. The method requires a  [AuthenticationDeallocationCallback](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationDeallocationCallback.html) instance as a parameter and calls `onAuthenticationDeallocated` once the SDK is deallocated. The checkDeallocation method should only be called once the SDK has returned a result and another SDK instance is required.

#### Dependency change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.12~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.14

#### Change in initate method
The enrollmentTransactionReference parameter has been moved to its own [setter](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setEnrollmentTransactionReference-java.lang.String-). It needs to be called before the initiate method is called.
In case an Authentication transaction has been created via the facemap server to server API [setAuthenticationTransactionReference](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setAuthenticationTransactionReference-java.lang.String-) should be used. Therefore [setEnrollmentTransactionReference](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/AuthenticationSDK.html#setEnrollmentTransactionReference-java.lang.String-) should not be called.

#### New SDK localizations added
SDK Translations for the languages Italian and Portuguese have been added.

## 3.2.1
#### Fixed a face scanning problem in which a black screen was shown to the user

## 3.2.0
#### Dependency change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2
*  ~~com.facetec:zoom-authentication-hybrid:7.0.9~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.12

#### API changes in AuthenticationCustomScanInterface
A new parameter [AuthenticationCancelReason](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCancelReason.html) was added to [AuthenticationCustomScanInterface$onAuthenticationScanCanceled](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html#onAuthenticationScanCanceled-com.jumio.auth.custom.AuthenticationCancelReason-)

#### Close button customization in custom ui
The position and image of the close button for can now be customized. Please have a look at the [AuthenticationCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanView.html)

#### Custom ui help animation support
[AuthenticationCustomSDKController$getHelpAnimation](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#getHelpAnimation-com.jumio.auth.custom.AuthenticationCustomAnimationView-) has been added to get the specific help animation in case the scan part is canceled. An instance of  [AuthenticationCustomAnimationView](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomAnimationView.html) needs to be passed where the animation is rendered in.

## 3.1.0
#### Dependency change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.5~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.9

## 3.0.0
Introduction of the Authentication product

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
