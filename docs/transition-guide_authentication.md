![Fastfill & Netverify](images/netverify.png)

# Transition guide for Authentication SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

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
