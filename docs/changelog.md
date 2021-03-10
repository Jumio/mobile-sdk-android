[![Version](https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?style=flat)](#release-notes)
[![License](https://img.shields.io/badge/license-commercial-3D3D3D?style=flat)](#copyright)
[![Platform](https://img.shields.io/badge/platform-Android-lightgrey?style=flat)](#general-requirements)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmobile-sdk.jumio.com%2Fcom%2Fjumio%2Fandroid%2Fcore%2Fmaven-metadata.xml?style=flat)](#integration)
[![API Level](http://img.shields.io/badge/API%20Level-19+-orange?style=flat)](#general-requirements)

# Change Log
All notable changes, such as SDK releases, updates and fixes, are documented in this file.

## SDK Version: __3.9.1__
![Changes](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 6.3.1, which fixes cross-dependency problems with OkHttp 4.x versions [Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved customization options [Identity Verification]

## SDK Version: __3.9.0__
![Improvement](https://img.shields.io/badge/Improvement-green) Improved SDK lifecycle and state handling to reduce specific scenarios in which SDK crashes could have happened [Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved retry guidance for Identity Verification [Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved customization options [Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added more granular differentiations for `ScanMode` in CustomUI [Identity Verification]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed rare issue that caused "Blur Hint" toast being displayed multiple times on certain devices [Identity Verification]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed possible Camera Exception using CustomUI [ID Verification/Fastfill, Identity Verification, Authentication]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed possible app crash when calling `NetverifyCustomSDKController.retry()` [ID Verification/Fastfill, Identity Verification]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed Zoom Authentication 412 error handling, preventing user from getting stuck in certain scenarios [Identity Verification]

![Changes](https://img.shields.io/badge/Change-blue) Removed deprecated Android Kotlin plugins [ID Verification/Fastfill, Identity Verification, Authentication, Document Verification]

![Changes](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 6.3.0, which includes accuracy improvements using Liveness Assurance [Identity Verification]

## SDK Version: __3.8.0__
![Improvement](https://img.shields.io/badge/Improvement-green) Added better guidance for devices with a fixed focal distance [ID Verification/Fastfill, Document Verification]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed crashes that could occur in edge cases [ID-Verification, Identity-Verification]

![Changes](https://img.shields.io/badge/Change-blue) Added iProov as an additional liveness vendor to the [Jumio KYX platform](https://www.jumio.com/kyx/) [Identity Verification]

## SDK Version: __3.7.3__
![Improvement](https://img.shields.io/badge/Improvement-green) New error code is returned in case an ad blocker or a firewall is detected [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added additional 3D Liveness customization options [ID Verification, Authentication]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed stroke color customization on negative action button [ID Verification/Fastfill, Authentication, Document Verification]

![Fixes](https://img.shields.io/badge/Fix-success) Fixed compatibility issues caused by Firebase Performance Plugin.

## SDK Version: __3.7.2__
![Fixes](https://img.shields.io/badge/Fix-success) Fixed a problem that face could not be captured anymore in certain cases [ID Verification Custom UI]

## SDK Version: __3.7.1__
![Fixes](https://img.shields.io/badge/Fix-success) Fixed problem in handling the user consent [ID Verification, Authentication]

## SDK Version: __3.7.0__
![Changes](https://img.shields.io/badge/Change-blue) Full redesign of NFC passport workflow [ID Verification]

![Changes](https://img.shields.io/badge/Change-blue) Update to newest 3D Liveness technology [ID Verification, Authentication]

![Changes](https://img.shields.io/badge/Change-blue) Adjusted Jumio logo and default color to reflect new Jumio appearance [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Support of 24 new languages [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Possibility to retrieve the captured images directly in the SDK [ID Verification/Fastfill]

## SDK Version: __3.6.2__
![Improvement](https://img.shields.io/badge/Improvement-green) Security enhancements [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

## SDK Version: __3.6.1__
![Fixes](https://img.shields.io/badge/Fix-success) Fixed wrong date parsing on magstripe encoded barcodes [Netverify/Fastfill]

## SDK Version: __3.6.0__
![Changes](https://img.shields.io/badge/Change-blue) Added support for right-to-left languages [Netverify/Fastfill, Authentication, Document Verification]

![Changes](https://img.shields.io/badge/Change-blue) Provide access to document guidance animation [Netverify Custom UI]

![Changes](https://img.shields.io/badge/Change-blue) Advanced custom UI sample implementation [Netverify Custom UI Sample]

![Changes](https://img.shields.io/badge/Change-blue) Adjusted handling of document types which don’t support plastic documents [Netverify]

![Improvement](https://img.shields.io/badge/Improvement-green) Support for 5 new languages (Czech, Greek, Hungarian, Polish, Romanian) [Netverify/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved accessibility handling [Netverify/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Reduced SDK size by ~1.5 MB [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

![Fixes](https://img.shields.io/badge/Fix-success)
Various smaller bug fixes/improvements [Netverify/Fastfill, Authentication, Document Verification]

## SDK Version: __3.5.0__
The previous release version 3.5.0 of the Jumio Mobile SDK is supported until 2020-08-04.

# Support
In case the support period is expired, no bug fixes and technical support are provided anymore. Current bugs are typically fixed in the upcoming versions.
Older SDK versions will keep functioning with our server until further notice, but we highly recommend to always update to the latest version to benefit from SDK improvements and bug fixes.

## Contact
If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. [Check it out at here.](https://support.jumio.com.)
