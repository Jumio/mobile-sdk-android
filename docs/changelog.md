![Header Graphic](images/jumio_feature_graphic.jpg)

[![Version](https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?style=flat)](../README.md#release-notes)
[![API Doc](https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?label=API%20doc&color=green&style=flat)](https://jumio.github.io/mobile-sdk-android/)
[![License](https://img.shields.io/badge/license-commercial-3D3D3D?style=flat)](../README.md#copyright)
[![Platform](https://img.shields.io/badge/platform-Android-lightgrey?style=flat)](../README.md#general-requirements)
[![API Level](https://img.shields.io/badge/API%20level-21+-orange?style=flat)](../README.md#general-requirements)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.mobile.jumio.ai%2Fcom%2Fjumio%2Fandroid%2Fcore%2Fmaven-metadata.xml?style=flat)](../README.md#integration)

[Improvement]: https://img.shields.io/badge/Improvement-green "Improvement shield"
[Change]: https://img.shields.io/badge/Change-blue "Change shield"
[Fix]: https://img.shields.io/badge/Fix-success "Fix shield"

# Change Log
All notable changes, such as SDK releases, updates and fixes, are documented in this file.
For detailed technical changes please refer to our [Transition Guide](transition_guide.md).

## Support Period
Current SDK version: __4.12.0__

Please refer to our [SDK maintenance and support policy](maintenance_policy.md) for more information about Mobile SDK maintenance and support.

## SDK Version: __4.12.0__

![Improvement] Added support for Jumio Liveness Premium with enhanced deepfake detection [Selfie Verification]

![Improvement] Added support for Brazilian Digital Driver's License [ID Verification]

![Fix] Multiple bug fixes and improvements

![Change] Updated Android sample application UI, transitioning from XML-based layouts to using Jetpack Compose 

## SDK Version: __4.11.0__
![Improvement] Added tilted image capture for frontside of ID documents. Enhanced checks of certain document security features [ID Verification]

![Improvement] Added unsupported documents check to improve quality of extracted data and improve user experience [ID Verification]

![Improvement] Added an updated Authentication Service [Selfie Verification]

![Fix] Fixed issues with code obfuscation

## SDK Version: __4.10.0__
![Improvement] Support for 4k Image capture. Improved ML model input, enhanced image and fraud checks [ID Verification]

![Improvement] Added flash capture for frontside of ID documents. Enhanced checks of certain document security features [ID Verification]

![Improvement] Support for Serbian language, for both Cyrillic and Latin [ID Verification, Selfie Verification, Document Verification]

## SDK Version: __4.9.1__
![Fix] Fixed a rare issue that could lead to a crash when the SDK is recreated

## SDK Version: __4.9.0__
![Improvement] Automated document and country selection, powered by classifer ML model [ID Verification]

![Improvement] Added possibility to pre-load required ML models. For more information checkout the according section in the [README](../README.md#ml-models) [ID Verification, Selfie Verification]

![Improvement] Major UI Redesign [ID Verification, Selfie Verification, Document Verification]

![Improvement] Improved Liveness retry logic. Prepared for granular instant feedback, if configured accordingly [Selfie Verification]

![Improvement] iProov SDK version update to 9.0.3 [Selfie Verification] 

![Improvement] Added support for R8's `fullMode` for code shrinking and obfuscation

![Change] Removed Device Risk module from SDK [Selfie Verification]

## SDK Version: __4.8.2__
![Improvement] iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: __4.8.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.8.0__
![Improvement] Managing Liveness dependencies to help better conversion [Selfie Verification]

## SDK Version: __4.7.2__
![Improvement] iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: __4.7.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.7.0__
![Improvement] Added support for [CameraX](https://developer.android.com/training/camerax)

![Improvement] Datadog SDK version update to 2.0: Added possibility to have a dedicated Jumio Datadog instance

![Improvement] Improved Jumio Liveness capturing experience [Selfie Verification]

![Change] Updated Jumio Liveness module [Selfie Verification]

![Change] Removed previous scanning functionalities, now all included in Autocapture functionality [ID Verification]

![Change] Removed Microblink barcode scanning, switched to MLkit [ID Verification]

![Fix] Fixed Liveness customization bug [Selfie Verification]

## SDK Version: __4.6.2__
![Improvement] iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: __4.6.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.6.0__
![Improvement] Added Jumio Liveness module to enhance the Liveness user experience and interface (Selfie Verification)

![Improvement] Improved Liveness customization options (Selfie Verification)

## SDK Version: __4.5.2__
![Improvement] iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: __4.5.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.5.0__
![Improvement] Added possibility for users to verify their identity using [Digital Identity](../README.md#digital-identity) [ID Verification, Selfie Verification]

![Improvement] iProov SDK version update to 8.3.1 [Selfie Verification]

![Improvement] Improved user consent handling [ID Verification, Selfie Verification]
<details>     
<summary>More details</summary>       

### User consent
User consent is now acquired for all users to ensure the accordance with biometric data protection laws. Please also refer to the [User Consent section](integration_faq.md#user-consent) in our FAQ.
</details>   

![Fix] Bug fixes: UI bugs, passport scanning issue for certain countries [ID Verification]

## SDK Version: __4.4.2__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.4.1__

![Fix] Bug fix: Internal crashes for certain edge cases

## SDK Version: __4.4.0__

![Improvement] Fully redesigned ID Autocapture experience - seamless capturing, precise guidance and faster user journey [ID Verification]

![Improvement] Major iProov SDK version update to 8.0.3 - no more face scanning filter, improved UI and more customization options [Selfie Verification]

![Improvement] Mandatory NFC scanning option [ID Verification]

![Fix] Bug fixes: UI bugs, internal crashes

## SDK Version: __4.3.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.3.0__

![Improvement] Autocapture functionality (introduced in SDK 4.2.0) is no longer in beta stage [ID Verification]

![Improvement] [Document Verification](../README.md#document-verification) functionality added

![Improvement] Improved user guidance: Clear distinction between scanning frontside or backside of ID document [ID Verification]

![Change] iProov SDK version update to 7.5.0 [Selfie Verification]

![Change] The SDK's minSdkVersion has been increased to 21 (Lollipop). Please check the [Transition Guide](transition_guide.md) for details.

![Fix] UI bugs, internal crashes [Selfie Verification]

## SDK Version: __4.2.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.2.0__
![Improvement] Alignment of previously existing scanning method and improved user experience through addition of Autocapture module (Beta) [ID Verification]

![Improvement] Support for device fingerprint capability [ID Verification, Selfie Verification]

![Improvement] Addition of NFC image extraction for similarity check [ID Verification]

![Improvement] Improved liveness customization: Centered Floating prompt for better user guidance during face scanning [Selfie Verification]

![Fix] Bug fixes: UI bugs, internal crashes

## SDK Version: __4.1.1__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.1.0__
![Improvement] Improved, granular user feedback for improved user experience and workflow through addition of Instant Feedback [ID Verification, Selfie Verification]

![Improvement] Support for Dark Mode for DefaultUI and CustomUI [ID Verification, Selfie Verification]

![Improvement] Addition of optional Datadog diagnostics module for monitoring SDK behavior and performance, as well as more efficient troubleshooting

![Change] iProov SDK version update to 7.2.0 [Selfie Verification]

![Fix] Bug fixes: UI bugs, security improvements, internal crashes

## SDK Version: __4.0.0__
This is a complete rewrite of our SDK. The SDK was built with CustomUI as a basis and restructured to align Android and iOS to reduce overall complexity and integration effort.

![Improvement] Improved security by switching to one-time authorization tokens for SDK initialization instead of relying on API token and secret

![Improvement] Redesigned Default UI flow

![Improvement] Slimline SDK configuration of only 1.8 MB size

![Improvement] Improved data extraction via enhancing the SDK capabilities with server-side extraction capabilities

![Improvement] Manual capture is now available as a fallback option for all other capture methods

## SDK Version: __3.9.5__
![Fix] Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __3.9.4__
![Changes](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 6.4.3 [Selfie Verification]

## SDK Version: __3.9.3__
![Changes](https://img.shields.io/badge/Improvement-green) Internal dependency update [Selfie Verification]

## SDK Version: __3.9.2__
![Change] iProov SDK version update to 6.4.1, which improves performance and offers additional customization options  [Selfie Verification]

## SDK Version: __3.9.1__
![Change] iProov SDK version update to 6.3.1, which fixes cross-dependency problems with OkHttp 4.x versions [Selfie Verification]

![Improvement] Improved customization options [Selfie Verification]

## SDK Version: __3.9.0__
![Improvement] Improved SDK lifecycle and state handling to reduce specific scenarios in which SDK crashes could have happened [Selfie Verification]

![Improvement] Improved retry guidance for Selfie Verification [Selfie Verification]

![Improvement] Improved customization options [Selfie Verification]

![Improvement] Added more granular differentiations for `ScanMode` in CustomUI [Selfie Verification]

![Fix] Fixed rare issue that caused "Blur Hint" toast being displayed multiple times on certain devices [Selfie Verification]

![Fix] Fixed possible Camera Exception using CustomUI [ID Verification/Fastfill, Selfie Verification, Authentication]

![Fix] Fixed possible app crash when calling `NetverifyCustomSDKController.retry()` [ID Verification/Fastfill, Selfie Verification]

![Fix] Fixed Zoom Authentication 412 error handling, preventing user from getting stuck in certain scenarios [Selfie Verification]

![Change] Removed deprecated Android Kotlin plugins [ID Verification/Fastfill, Selfie Verification, Authentication, Document Verification]

![Change] iProov SDK version update to 6.3.0, which includes accuracy improvements using Liveness Assurance [Selfie Verification]

## SDK Version: __3.8.0__
![Improvement] Added better guidance for devices with a fixed focal distance [ID Verification/Fastfill, Document Verification]

![Fix] Fixed crashes that could occur in edge cases [ID-Verification, Identity-Verification]

![Change] Added iProov as an additional liveness vendor to the [Jumio KYX platform](https://www.jumio.com/kyx/) [Selfie Verification]

## SDK Version: __3.7.3__
![Improvement] New error code is returned in case an ad blocker or a firewall is detected [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement] Added additional 3D Liveness customization options [ID Verification, Authentication]

![Fix] Fixed stroke color customization on negative action button [ID Verification/Fastfill, Authentication, Document Verification]

![Fix] Fixed compatibility issues caused by Firebase Performance Plugin.

## SDK Version: __3.7.2__
![Fix] Fixed a problem that face could not be captured anymore in certain cases [ID Verification Custom UI]

## SDK Version: __3.7.1__
![Fix] Fixed problem in handling the user consent [ID Verification, Authentication]

## SDK Version: __3.7.0__
![Change] Full redesign of NFC passport workflow [ID Verification]

![Change] Update to newest 3D Liveness technology [ID Verification, Authentication]

![Change] Adjusted Jumio logo and default color to reflect new Jumio appearance [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement] Support of 24 new languages [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement] Possibility to retrieve the captured images directly in the SDK [ID Verification/Fastfill]

## SDK Version: __3.6.2__
![Improvement] Security enhancements [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

## SDK Version: __3.6.1__
![Fix] Fixed wrong date parsing on magstripe encoded barcodes [Netverify/Fastfill]

## SDK Version: __3.6.0__
![Change] Added support for right-to-left languages [Netverify/Fastfill, Authentication, Document Verification]

![Change] Provide access to document guidance animation [Netverify Custom UI]

![Change] Advanced custom UI sample implementation [Netverify Custom UI Sample]

![Change] Adjusted handling of document types which don’t support plastic documents [Netverify]

![Improvement] Support for 5 new languages (Czech, Greek, Hungarian, Polish, Romanian) [Netverify/Fastfill, Authentication, Document Verification]

![Improvement] Improved accessibility handling [Netverify/Fastfill, Authentication, Document Verification]

![Improvement] Reduced SDK size by ~1.5 MB [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

![Fix] Various smaller bug fixes/improvements [Netverify/Fastfill, Authentication, Document Verification]

## Contact
If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. [Check it out at here.](https://support.jumio.com.)
