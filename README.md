![Jumio](docs/images/jumio_feature_graphic.jpg)

[![Version](https://img.shields.io/github/v/release/Jumio/mobile-sdk-android?style=flat)](#release-notes)
[![License](https://img.shields.io/badge/license-commercial-3D3D3D?style=flat)](#copyright)
[![Platform](https://img.shields.io/badge/platform-Android-lightgrey?style=flat)](#general-requirements)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmobile-sdk.jumio.com%2Fcom%2Fjumio%2Fandroid%2Fcore%2Fmaven-metadata.xml?style=flat)](#integration)
[![API Level](http://img.shields.io/badge/API%20Level-19+-orange?style=flat)](#general-requirements)

# Table of Content
- [Overview](#overview)
- [Get Started](#get-started)
    - [ID Verification & Fastfill SDK](docs/integration_id-verification-fastfill.md)
    - [Authentication SDK](docs/integration_authentication.md)
    - [Document Verification SDK](docs/integration_document-verification.md)
    - [BAM Checkout SDK](docs/integration_bam-checkout.md)
- [Quickstart](#quickstart)
- [Basics](#basics)
    - [General Requirements](#general-requirements)
    - [Permissions](#permissions)
    - [Integration](#integration)
    - [Proguard](#proguard)
    - [Language Localization](#language-localization)
- [Release Notes](#release-notes)
- [Support](#support)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)
- [FAQ](docs/integration_faq.md)
- [Known Issues](docs/known_issues.md)

# Overview
The Jumio Software Development Kit (SDK) provides you with a set of tools and UIs (default or custom) to develop an Android application perfectly fitted to your specific needs.

Onboard new users and easily verify their digital identities, by making sure the IDs provided by them are valid and authentic.  Extract data from IDs and credit cards completely automatically and within seconds. Confirm users really are who they say they are by having them take a quick selfie and match it to their respective documents. Jumio uses cutting-edge biometric technology such as 3D face mapping to make sure there is an actual, real-life person in front of the screen.

![DL EU](docs/images/images_overview/driver_license_EU.png)  ![DL US](docs/images/images_overview/driver_license_US.png)  ![Manual Capture](docs/images/images_overview/manual_capture.png)

Using the Jumio SDK will allow you to create the best possible solution for your individual needs, providing you with a range of different services to choose from.

# Get started
Please note that [basic setup](#basics) is required before continuing with the integration of any of the following services.

## Jumio ID Verification and Fastfill
ID Verification (formerly known as Netverify) is a secure and easy solution that allows you to establish the genuine identity of your users in your mobile application, by verifying their passports, government-issued IDs and VISA in real-time. Very user-friendly and highly customizable, it makes onboarding new customers quick and simple.

- [Integration ID Verification & Fastfill SDK](docs/integration_id-verification-fastfill.md)

## Jumio Authentication
Authentication is a cutting-edge biometric-based service that establishes digital identities of your users, simply by taking a selfie. Advanced 3D face mapping-technology quickly and securely authenticates users and their digital identities.

- [Integration Authentication SDK](docs/integration_authentication.md)

## Jumio Document Verification
Document Verification is a powerful solution that allows users to scan various types of documents quickly and easily in your mobile application. Data extraction is already supported for various document types, such as bank statements.

- [Integration Document Verification SDK](docs/integration_document-verification.md)

## Jumio BAM Checkout
BAM Checkout enables you to extract data from your customer's credit card and/or ID in your mobile application within seconds. Every checkout flow is fully automated, no manual input necessary!

- [Integration BAM Checkout SDK](docs/integration_bam-checkout.md)

# Quickstart
This section helps you get started with the Jumio Mobile SDK using the [Android sample application](sample). Before you begin, you must have: 

* A __commercial Jumio license__ to successfully run our examples; please contact sales@jumio.com for details. 
* A __current Android Studio version__ to open and try out the sample project. 

If you'd like to watch a video demo of this quick start, click [here](https://share.vidyard.com/watch/oAUXU1EWXco1mPUePz7Ue6).

## Download the sample application
Start by downloading the [Android sample application](sample) from the Jumio Github repo. You can do this either by cloning the repository (using SHH oder HTTPS) to your local device, or simply downloading everything as a ZIP. 

## Import the sample into Android Studio
Once you’ve got the sample application downloaded and unzipped if necessary, open Android Studio. You’ll be faced with a couple of options. Choose __Import project__ and navigate to where you’ve saved your sample application. Select the __JumioMobileSample folder__ and open it.

Android Studio will now start to import the project. This might take a bit of time. Make sure to wait until the Grade Build has finished and the application is properly installed!

## Explore the packages
The Android sample application contains the packages `com.jumio.sample.java` and `com.jumio.sample.kotlin`. Both of those packages contain the exact same thing, in Java and Kotlin respectively. If you'd like to switch between them, you'll have to switch the `<intent-filter>` in the `AndroidManifest.xml` from one `MainActivity` to the other. As a default, the filter is set for Kotlin, see the code example down below:

```
<!-- KOTLIN -->
<activity
    android:name="com.jumio.sample.kotlin.MainActivity"
    android:configChanges="orientation|screenSize|screenLayout|keyboardHidden|uiMode|layoutDirection"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">

    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
    </intent-filter>

</activity>
```

In each of the two packages, Java and Kotlin, you will find:
* `MainActivity`
* `authentication`
* `bam`
* `document-verification`
* `netverify`
    * `customui`

Each of these folders contains a corresponding fragment. In each fragment, the most important methods for this service are shown and quickly outlined. If you’re interested in seeing how the ID Verification Custom UI works, check out the `customui` folder inside `netverify`, which shows an example of how a custom implementation could be done and what it could look like.

## Configure your credentials
Right at the top of the `MainActivity` you’ll find the following empty parameters:

`private static String NETVERIFY_API_TOKEN`
`private static String NETVERIFY_API_SECRET`

You can find your customer API token and API secret in the Customer Portal on the __Settings__ page under __API credentials__. Add your individual key and token instead of the placeholder `""`. The default setting for the data center is `JumioDataCenter.US`. If you're using [BAM Checkout](docs/integration_bam-checkout.md), you'll need to use the BAM API token and BAM API secret.

__Note:__ We strongly recommend storing all credentials outside of your app! We suggest to not hardcode them within your application but load them during runtime from your server-side implementation.

## Try it out
Once you start up the sample application, you'll be given the option of trying out ID Verification. Select the hamburger menu in the top-left corner to try out something else. Your application will also need camera permission, which will be prompted for automatically once you try to start any of services. If you deny camera permissions, you won't be able to use any of the services.

# Basics

## General Requirements
The minimum requirements for the SDK are:
*	Android 4.4 (API level 19) or higher
*	Internet connection

The following architectures are supported in the SDK:
*	ARMv7 processor with Neon
*	ARM64-v8a

__Note:__ Currently, x86 and x86_64 are *not* supported. You get an *UnsatisfiedLinkError* if app and  CPU architecture do not match, or the CPU architecture is not supported.

You will require a __commercial Jumio License__ to run any of our examples. Please refer to sales@jumio.com for details.

## Permissions
Required permissions are linked automatically by the SDK.

The following permissions are optional:
```
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-feature android:name="android.hardware.camera" android:required="false"/>
```

__Note:__ On devices running Android Marshmallow (6.0) and above, you need to acquire `android.permissions.CAMERA` dynamically before initializing the SDK.

Use `getRequiredPermissions()` to get a list of all required permissions.
```
public static String[] getRequiredPermissions();
```

## Integration
Use the SDK in your application by including the Maven repositories with the following `build.gradle` configuration in Android Studio:

```
repositories {
	google()
	jcenter()
	maven { url 'https://mobile-sdk.jumio.com' }
}
```
Check the Android Studio [sample projects](sample/JumioMobileSample/) to learn the most common use.


## Proguard
One Proguard keep rule has to be added to the Jumio Mobile SDK:
```
-keep class com.jumio.** { *; }
```
Most of the Proguard settings are applied automatically as they are defined as consumer proguard rules within the SDK.
The current rules can also be found in the [Sample app](sample/JumioMobileSample/).

## Language Localization
Our SDK supports the [default Android localization features](https://developer.android.com/training/basics/supporting-devices/languages.html) for different languages.
All label texts and button titles in the SDK can be changed and localized by adding the required Strings you want to change in a `strings.xml` file in a `values` directory for the language and culture preference that you want to support. You can check out strings that are modifiable [within our Sample application](sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml).

For our products ID Verification & Fastfill, Authentication & Document Verification we support following languages for your convenience:

_Afrikaans, Arabic, Bulgarian, Chinese(Simplified), Chinese(Traditional), Croatian, Czech, Danish, Dutch, Estonian, English, Finnish, French, German, Greek, Hindi, Hungarian, Indonesian, Italian, Japanese, Khmer, Korean, Latvian, Lithuanian, Maltese, Norwegian, Polish, Portuguese, Romanian, Russian, Slovak, Slovenian, Spanish, Swedish, Thai, Turkish, Vietnamese, Zulu_

Our SDK supports accessibility features. Visually impaired users can now enable __TalkBack__ or increase the __text size__ on their device. The accessibility-strings that are used by TalkBack contain *accessibility* in their key and can be also modified in the `strings.xml`.

# Release Notes
Please refer to our [Change Log](docs/changelog.md) for more information about our current SDK version and further details.

# Support

## Previous Version
The previous release version 3.7.1 of the Jumio Mobile SDK is supported until 2020-11-30.

In case the support period is expired, no bug fixes and technical support are provided anymore. Current bugs are typically fixed in the upcoming versions.
Older SDK versions will keep functioning with our server until further notice, but we highly recommend to always update to the latest version to benefit from SDK improvements and bug fixes.

## Two-factor Authentication
If you want to enable two-factor authentication for your Jumio customer portal [please contact us.](https://support.jumio.com) Once enabled, users will be guided through the setup upon their first login to obtain a security code using the "Google Authenticator" app.

## Licenses
The software contains third-party open source software. For more information, please see [licenses](licenses).

This software is based in part on the work of the Independent JPEG Group.

## Contact
If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. [Check it out at here.](https://support.jumio.com).

## Copyright
&copy; Jumio Corporation, 395 Page Mill Road, Suite 150, Palo Alto, CA 94306

The source code and software available on this website (“Software”) is provided by Jumio Corp. or its affiliated group companies (“Jumio”) "as is” and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall Jumio be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including but not limited to procurement of substitute goods or services, loss of use, data, profits, or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this Software, even if advised of the possibility of such damage.
In any case, your use of this Software is subject to the terms and conditions that apply to your contractual relationship with Jumio. As regards Jumio’s privacy practices, please see our privacy notice available here: [Privacy Policy](https://www.jumio.com/legal-information/privacy-policy/).
