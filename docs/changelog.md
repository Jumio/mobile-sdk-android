![Header Graphic](images/jumio_feature_graphic.jpg)

[![Version](https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?style=flat)](#release-notes)
[![License](https://img.shields.io/badge/license-commercial-3D3D3D?style=flat)](#copyright)
[![Platform](https://img.shields.io/badge/platform-Android-lightgrey?style=flat)](#general-requirements)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.mobile.jumio.ai%2Fcom%2Fjumio%2Fandroid%2Fcore%2Fmaven-metadata.xml?style=flat)](#integration)
[![API Level](http://img.shields.io/badge/API%20Level-21+-orange?style=flat)](#general-requirements)

[Improvement]: https://img.shields.io/badge/Improvement-green "Improvement shield"
[Change]: https://img.shields.io/badge/Change-blue "Change shield"
[Fix]: https://img.shields.io/badge/Fix-success "Fix shield"

# Change Log
All notable changes, such as SDK releases, updates and fixes, are documented in this file.
For detailed technical changes please refer to our [Transition Guide](transition_guide.md).

## Support Period
Current SDK version: __4.3.1__
The previous release version 4.2.0 of the Jumio Mobile SDK is supported until 2022-11-28.

When the support period has expired, bug fixes and technical support will no longer be provided. Current bugs are typically fixed in the upcoming versions. __Older SDK versions will keep functioning with our server until further notice,__ but we highly recommend you always update to the latest version to benefit from SDK improvements and bug fixes.

## SDK Version: __4.3.1__
![Fixes](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.3.0__

![Improvement] Autocapture functionality (introduced in SDK 4.2.0) is no longer in beta stage [ID Verification]

![Improvement] [Document Verification](../README.md#document-verification) functionality added

![Improvement] Improved user guidance: Clear distinction between scanning frontside or backside of ID document [ID Verification]

![Change] iProov SDK version update to 7.5.0 [Identity Verification]

![Fix] UI bugs, internal crashes [Identity Verification]

## SDK Version: __4.2.0__
![Improvement] Alignment of previously existing scanning method and improved user experience through addition of Autocapture module (Beta) [ID Verification]

![Improvement] Support for device fingerprint capability [ID Verification, Identity Verification]

![Improvement] Addition of NFC image extraction for similarity check [ID Verification]

![Improvement] Improved liveness customization: Centered Floating prompt for better user guidance during face scanning [Identity Verification]

![Fix] Bug fixes: UI bugs, internal crashes

## SDK Version: __4.1.1__
![Fixes](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __4.1.0__
![Improvement] Improved, granular user feedback for improved user experience and workflow through addition of Instant Feedback [ID Verification, Identity Verification]

![Improvement] Support for Dark Mode for DefaultUI and CustomUI [ID Verification, Identity Verification]

![Improvement] Addition of optional Datadog diagnostics module for monitoring SDK behavior and performance, as well as more efficient troubleshooting

![Change] iProov SDK version update to 7.2.0 [Identity Verification]

![Fix] Bug fixes: UI bugs, security improvements, internal crashes

## SDK Version: __4.0.0__
This is a complete rewrite of our SDK. The SDK was built with CustomUI as a basis and restructured to align Android and iOS to reduce overall complexity and integration effort.

![Improvement] Improved security by switching to one-time authorization tokens for SDK initialization instead of relying on API token and secret

![Improvement] Redesigned Default UI flow

![Improvement] Slimline SDK configuration of only 1.8 MB size

![Improvement] Improved data extraction via enhancing the SDK capabilities with server-side extraction capabilities

![Improvement] Manual capture is now available as a fallback option for all other capture methods

## SDK Version: __3.9.5__
![Fixes](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: __3.9.4__
![Changes](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 6.4.3 [Identity Verification]

## SDK Version: __3.9.3__
![Changes](https://img.shields.io/badge/Improvement-green) Internal dependency update [Identity Verification]

## SDK Version: __3.9.2__
![Change] iProov SDK version update to 6.4.1, which improves performance and offers additional customization options  [Identity Verification]

## SDK Version: __3.9.1__
![Change] iProov SDK version update to 6.3.1, which fixes cross-dependency problems with OkHttp 4.x versions [Identity Verification]

![Improvement] Improved customization options [Identity Verification]

## SDK Version: __3.9.0__
![Improvement] Improved SDK lifecycle and state handling to reduce specific scenarios in which SDK crashes could have happened [Identity Verification]

![Improvement] Improved retry guidance for Identity Verification [Identity Verification]

![Improvement] Improved customization options [Identity Verification]

![Improvement] Added more granular differentiations for `ScanMode` in CustomUI [Identity Verification]

![Fix] Fixed rare issue that caused "Blur Hint" toast being displayed multiple times on certain devices [Identity Verification]

![Fix] Fixed possible Camera Exception using CustomUI [ID Verification/Fastfill, Identity Verification, Authentication]

![Fix] Fixed possible app crash when calling `NetverifyCustomSDKController.retry()` [ID Verification/Fastfill, Identity Verification]

![Fix] Fixed Zoom Authentication 412 error handling, preventing user from getting stuck in certain scenarios [Identity Verification]

![Change] Removed deprecated Android Kotlin plugins [ID Verification/Fastfill, Identity Verification, Authentication, Document Verification]

![Change] iProov SDK version update to 6.3.0, which includes accuracy improvements using Liveness Assurance [Identity Verification]

## SDK Version: __3.8.0__
![Improvement] Added better guidance for devices with a fixed focal distance [ID Verification/Fastfill, Document Verification]

![Fix] Fixed crashes that could occur in edge cases [ID-Verification, Identity-Verification]

![Change] Added iProov as an additional liveness vendor to the [Jumio KYX platform](https://www.jumio.com/kyx/) [Identity Verification]

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
