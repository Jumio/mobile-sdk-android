![Header Graphic](images/jumio_feature_graphic.jpg)

<div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', alignItems: 'center' }}>
  <a href="../README.md#release-notes">
    <img src="https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?style=flat" alt="Version" />
  </a>
  <a href="https://jumio.github.io/mobile-sdk-android/">
    <img src="https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?label=API%20doc&color=green&style=flat" alt="API Doc" />
  </a>
  <a href="../README.md#copyright">
    <img src="https://img.shields.io/badge/license-commercial-3D3D3D?style=flat" alt="License" />
  </a>
  <a href="../README.md#general-requirements">
    <img src="https://img.shields.io/badge/platform-Android-lightgrey?style=flat" alt="Platform" />
  </a>
  <a href="../README.md#general-requirements">
    <img src="https://img.shields.io/badge/API%20level-23+-orange?style=flat" alt="API Level" />
  </a>
  <a href="../README.md#integration">
    <img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.mobile.jumio.ai%2Fcom%2Fjumio%2Fandroid%2Fcore%2Fmaven-metadata.xml?style=flat" alt="Maven" />
  </a>
</div>

[Improvement]: https://img.shields.io/badge/Improvement-green 'Improvement shield'
[Change]: https://img.shields.io/badge/Change-blue 'Change shield'
[Fix]: https://img.shields.io/badge/Fix-success 'Fix shield'

# Change Log

All notable changes, such as SDK releases, updates and fixes, are documented in this file.
For detailed technical changes please refer to our [Transition Guide](transition_guide.md).

## Support Period

Current SDK version: 4.16.0

Please refer to our [SDK maintenance and support policy](maintenance_policy.md) for more information about Mobile SDK maintenance and support.

## SDK Version: 4.16.0

![Improvement](https://img.shields.io/badge/Improvement-green) Redesign of the ID Autocapture user experience.

![Improvement](https://img.shields.io/badge/Improvement-green) Support for image upload for DocProof workflows.

![Improvement](https://img.shields.io/badge/Improvement-green) Support for Liveness capture using back camera.

## SDK Version: 4.15.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for NFC read-only scanning.

![Improvement](https://img.shields.io/badge/Improvement-green) Introduced a configurable max retry count for NFC scanning.

![Improvement](https://img.shields.io/badge/Improvement-green) Included NFC scanning result status in transaction details via the Retrieval API.

![Improvement](https://img.shields.io/badge/Improvement-green) Enhanced user experience for NFC scanning with automatic NFC chip location detection.

## SDK Version: 4.14.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added enhanced virtual camera injection detection [ID Verification, Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for Digital Identity using eIDAS for selected countries [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Accessibility updates for compliance with WCAG 2.2 AA and EAA.

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for Digital Identity using eIDAS for selected countries [ID Verification]

![Fix](https://img.shields.io/badge/Fix-success) Added `kotlin.Pair` to `consumer-rules.pro` for SDK Wrapper

## SDK Version: 4.13.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for NFC Extraction of IDs

![Fix](https://img.shields.io/badge/Fix-success) Various bug fixes and improvements

## SDK Version: 4.12.1

![Fix](https://img.shields.io/badge/Fix-success) Rare crashes in Jumio Liveness

![Fix](https://img.shields.io/badge/Fix-success) Liveness Images not available

![Fix](https://img.shields.io/badge/Fix-success) Issues with CameraX not selecting a Camera

## SDK Version: 4.12.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for Jumio Liveness Premium with enhanced deepfake detection [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for Brazilian Digital Driver's License [ID Verification]

![Fix](https://img.shields.io/badge/Fix-success) Multiple bug fixes and improvements

![Change](https://img.shields.io/badge/Change-blue) Updated Android sample application UI, transitioning from XML-based layouts to using Jetpack Compose

## SDK Version: 4.11.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added tilted image capture for frontside of ID documents. Enhanced checks of certain document security features [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added unsupported documents check to improve quality of extracted data and improve user experience [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added an updated Authentication Service [Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed issues with code obfuscation

## SDK Version: 4.10.0

Added `kotlin.Pair` to `co
![Improvement](https://img.shields.io/badge/Improvement-green) Support for 4k Image capture. Improved ML model input, enhanced image and fraud checks [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added flash capture for frontside of ID documents. Enhanced checks of certain document security features [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Support for Serbian language, for both Cyrillic and Latin [ID Verification, Selfie Verification, Document Verification]

## SDK Version: 4.9.1

![Fix](https://img.shields.io/badge/Fix-success) Fixed a rare issue that could lead to a crash when the SDK is recreated

## SDK Version: 4.9.0

![Improvement](https://img.shields.io/badge/Improvement-green) Automated document and country selection, powered by classifer ML model [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added possibility to pre-load required ML models. For more information checkout the according section in the [README](../README.md#ml-models) [ID Verification, Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Major UI Redesign [ID Verification, Selfie Verification, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved Liveness retry logic. Prepared for granular instant feedback, if configured accordingly [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 9.0.3 [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for R8's `fullMode` for code shrinking and obfuscation

![Change](https://img.shields.io/badge/Change-blue) Removed Device Risk module from SDK [Selfie Verification]

## SDK Version: 4.8.2

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: 4.8.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.8.0

![Improvement](https://img.shields.io/badge/Improvement-green) Managing Liveness dependencies to help better conversion [Selfie Verification]

## SDK Version: 4.7.2

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: 4.7.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.7.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added support for [CameraX](https://developer.android.com/training/camerax)

![Improvement](https://img.shields.io/badge/Improvement-green) Datadog SDK version update to 2.0: Added possibility to have a dedicated Jumio Datadog instance

![Improvement](https://img.shields.io/badge/Improvement-green) Improved Jumio Liveness capturing experience [Selfie Verification]

![Change](https://img.shields.io/badge/Change-blue) Updated Jumio Liveness module [Selfie Verification]

![Change](https://img.shields.io/badge/Change-blue) Removed previous scanning functionalities, now all included in Autocapture functionality [ID Verification]

![Change](https://img.shields.io/badge/Change-blue) Removed Microblink barcode scanning, switched to MLkit [ID Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed Liveness customization bug [Selfie Verification]

## SDK Version: 4.6.2

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: 4.6.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.6.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added Jumio Liveness module to enhance the Liveness user experience and interface (Selfie Verification)

![Improvement](https://img.shields.io/badge/Improvement-green) Improved Liveness customization options (Selfie Verification)

## SDK Version: 4.5.2

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 8.5.2 [Selfie Verification]

## SDK Version: 4.5.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.5.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added possibility for users to verify their identity using [Digital Identity](../README.md#digital-identity) [ID Verification, Identity Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 8.3.1 [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved user consent handling [ID Verification, Selfie Verification]

<details>     
<summary>More details</summary>

### User consent

User consent is now acquired for all users to ensure the accordance with biometric data protection laws. Please also refer to the [User Consent section](integration_faq.md#user-consent) in our FAQ.

</details>

![Fix](https://img.shields.io/badge/Fix-success) Bug fixes: UI bugs, passport scanning issue for certain countries [ID Verification]

## SDK Version: 4.4.2

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.4.1

![Fix](https://img.shields.io/badge/Fix-success) Bug fix: Internal crashes for certain edge cases

## SDK Version: 4.4.0

![Improvement](https://img.shields.io/badge/Improvement-green) Fully redesigned ID Autocapture experience - seamless capturing, precise guidance and faster user journey [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Major iProov SDK version update to 8.0.3 - no more face scanning filter, improved UI and more customization options [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Mandatory NFC scanning option [ID Verification]

![Fix](https://img.shields.io/badge/Fix-success) Bug fixes: UI bugs, internal crashes

## SDK Version: 4.3.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.3.0

![Improvement](https://img.shields.io/badge/Improvement-green) Autocapture functionality (introduced in SDK 4.2.0) is no longer in beta stage [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) [Document Verification](../README.md#document-verification) functionality added.

![Improvement](https://img.shields.io/badge/Improvement-green) Improved user guidance: Clear distinction between scanning frontside or backside of ID document [ID Verification]

![Change](https://img.shields.io/badge/Change-blue) iProov SDK version update to 7.5.0 [Selfie Verification]

![Change](https://img.shields.io/badge/Change-blue) The SDK's minSdkVersion has been increased to 21 (Lollipop). Please check the [Transition Guide](transition_guide.md) for details.

![Fix](https://img.shields.io/badge/Fix-success) UI bugs, internal crashes [Selfie Verification]

## SDK Version: 4.2.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.2.0

![Improvement](https://img.shields.io/badge/Improvement-green) Alignment of previously existing scanning method and improved user experience through addition of Autocapture module (Beta) [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Support for device fingerprint capability [ID Verification, Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Addition of NFC image extraction for similarity check [ID Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved liveness customization: Centered Floating prompt for better user guidance during face scanning [Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Bug fixes: UI bugs, internal crashes

## SDK Version: 4.1.1

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 4.1.0

![Improvement](https://img.shields.io/badge/Improvement-green) Improved, granular user feedback for improved user experience and workflow through addition of Instant Feedback [ID Verification, Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Support for Dark Mode for DefaultUI and CustomUI [ID Verification, Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Addition of optional Datadog diagnostics module for monitoring SDK behavior and performance, as well as more efficient troubleshooting

![Change](https://img.shields.io/badge/Change-blue) iProov SDK version update to 7.2.0 [Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Bug fixes: UI bugs, security improvements, internal crashes

## SDK Version: 4.0.0

This is a complete rewrite of our SDK. The SDK was built with CustomUI as a basis and restructured to align Android and iOS to reduce overall complexity and integration effort.

![Improvement](https://img.shields.io/badge/Improvement-green) Improved security by switching to one-time authorization tokens for SDK initialization instead of relying on API token and secret

![Improvement](https://img.shields.io/badge/Improvement-green) Redesigned Default UI flow

![Improvement](https://img.shields.io/badge/Improvement-green) Slimline SDK configuration of only 1.8 MB size

![Improvement](https://img.shields.io/badge/Improvement-green) Improved data extraction via enhancing the SDK capabilities with server-side extraction capabilities

![Improvement](https://img.shields.io/badge/Improvement-green) Manual capture is now available as a fallback option for all other capture methods

## SDK Version: 3.9.5

![Fix](https://img.shields.io/badge/Fix-success) Removed Location handling to fix potential Google Play Store rejections

## SDK Version: 3.9.4

![Changes](https://img.shields.io/badge/Improvement-green) iProov SDK version update to 6.4.3 [Selfie Verification]

## SDK Version: 3.9.3

![Changes](https://img.shields.io/badge/Improvement-green) Internal dependency update [Selfie Verification]

## SDK Version: 3.9.2

![Change](https://img.shields.io/badge/Change-blue) iProov SDK version update to 6.4.1, which improves performance and offers additional customization options [Selfie Verification]

## SDK Version: 3.9.1

![Change](https://img.shields.io/badge/Change-blue) iProov SDK version update to 6.3.1, which fixes cross-dependency problems with OkHttp 4.x versions [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved customization options [Selfie Verification]

## SDK Version: 3.9.0

![Improvement](https://img.shields.io/badge/Improvement-green) Improved SDK lifecycle and state handling to reduce specific scenarios in which SDK crashes could have happened [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved retry guidance for Selfie Verification [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved customization options [Selfie Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added more granular differentiations for `ScanMode` in CustomUI [Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed rare issue that caused "Blur Hint" toast being displayed multiple times on certain devices [Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed possible Camera Exception using CustomUI [ID Verification/Fastfill, Selfie Verification, Authentication]

![Fix](https://img.shields.io/badge/Fix-success) Fixed possible app crash when calling `NetverifyCustomSDKController.retry()` [ID Verification/Fastfill, Selfie Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed Zoom Authentication 412 error handling, preventing user from getting stuck in certain scenarios [Selfie Verification]

![Change](https://img.shields.io/badge/Change-blue) Removed deprecated Android Kotlin plugins [ID Verification/Fastfill, Selfie Verification, Authentication, Document Verification]

![Change](https://img.shields.io/badge/Change-blue) iProov SDK version update to 6.3.0, which includes accuracy improvements using Liveness Assurance [Selfie Verification]

## SDK Version: 3.8.0

![Improvement](https://img.shields.io/badge/Improvement-green) Added better guidance for devices with a fixed focal distance [ID Verification/Fastfill, Document Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed crashes that could occur in edge cases [ID-Verification, Identity-Verification]

![Change](https://img.shields.io/badge/Change-blue) Added iProov as an additional liveness vendor to the [Jumio KYX platform](https://www.jumio.com/kyx/) [Selfie Verification]

## SDK Version: 3.7.3

![Improvement](https://img.shields.io/badge/Improvement-green) New error code is returned in case an ad blocker or a firewall is detected [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Added additional 3D Liveness customization options [ID Verification, Authentication]

![Fix](https://img.shields.io/badge/Fix-success) Fixed stroke color customization on negative action button [ID Verification/Fastfill, Authentication, Document Verification]

![Fix](https://img.shields.io/badge/Fix-success) Fixed compatibility issues caused by Firebase Performance Plugin.

## SDK Version: 3.7.2

![Fix](https://img.shields.io/badge/Fix-success) Fixed a problem that face could not be captured anymore in certain cases [ID Verification Custom UI]

## SDK Version: 3.7.1

![Fix](https://img.shields.io/badge/Fix-success) Fixed problem in handling the user consent [ID Verification, Authentication]

## SDK Version: 3.7.0

![Change](https://img.shields.io/badge/Change-blue) Full redesign of NFC passport workflow [ID Verification]

![Change](https://img.shields.io/badge/Change-blue) Update to newest 3D Liveness technology [ID Verification, Authentication]

![Change](https://img.shields.io/badge/Change-blue) Adjusted Jumio logo and default color to reflect new Jumio appearance [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Support of 24 new languages [ID Verification/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Possibility to retrieve the captured images directly in the SDK [ID Verification/Fastfill]

## SDK Version: 3.6.2

![Improvement](https://img.shields.io/badge/Improvement-green) Security enhancements [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

## SDK Version: 3.6.1

![Fix](https://img.shields.io/badge/Fix-success) Fixed wrong date parsing on magstripe encoded barcodes [Netverify/Fastfill]

## SDK Version: 3.6.0

![Change](https://img.shields.io/badge/Change-blue) Added support for right-to-left languages [Netverify/Fastfill, Authentication, Document Verification]

![Change](https://img.shields.io/badge/Change-blue) Provide access to document guidance animation [Netverify Custom UI]

![Change](https://img.shields.io/badge/Change-blue) Advanced custom UI sample implementation [Netverify Custom UI Sample]

![Change](https://img.shields.io/badge/Change-blue) Adjusted handling of document types which don’t support plastic documents [Netverify]

![Improvement](https://img.shields.io/badge/Improvement-green) Support for 5 new languages (Czech, Greek, Hungarian, Polish, Romanian) [Netverify/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Improved accessibility handling [Netverify/Fastfill, Authentication, Document Verification]

![Improvement](https://img.shields.io/badge/Improvement-green) Reduced SDK size by ~1.5 MB [Netverify/Fastfill, Authentication, Document Verification, BAM Checkout]

![Fix](https://img.shields.io/badge/Fix-success) Various smaller bug fixes/improvements [Netverify/Fastfill, Authentication, Document Verification]

## Contact

If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. [Check it out at here.](https://support.jumio.com.)
