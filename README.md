![Jumio](docs/images/jumio_feature_graphic.png)

# Table of Content
- [Release notes](#release-notes)
- [Basic Setup](#basic-setup)
- [Get started](#get-started)
- [Support](#support)

# Release notes
SDK version: 3.0.0

#### Changes
* Initial release of the [Jumio Authentication](https://www.jumio.com/authentication/) SDK [Authentication]
* Optimized 3D liveness handling to increase user conversion [Netverify]
* Added customUI support for 3D Liveness and Authentication [Netverify, Authentication]
* Improved MRZ reading accuracy [Netverify/Fastfill]
* Improved scanning experience for most European Driving licenses [Netverify/Fastfill]
* Renamed some public methods [Netverify/Fastfill, Document Verification]

#### Fixes
* Various smaller bug fixes/improvements [Netverify/Fastfill, Document Verification, BAM Checkout]

# Basic Setup

## General Requirements
The requirements for the SDK are:
*	Android 4.4 (API level 19) or higher
*	ARMv7 processor with Neon, ARM64-v8a
*	Internet connection

## Permissions
Required permissions are linked automatically by the SDK.

The following permissions are optional:
```
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-feature android:name="android.hardware.camera" android:required="false"/>
```

**Note:** On devices running Android Marshmallow (6.0) and above, you need to acquire `android.permissions.CAMERA` dynamically before initializing the SDK.

Use `getRequiredPermissions` to get a list of all required permissions.

```
public static String[] getRequiredPermissions();
```

## Proguard
If you are using Proguard, add the following lines in its configuration.

```
-keep class com.jumio.** { *; }
-keep class jumio.** { *; }
-keep class com.microblink.** { *; }
-keep class com.microblink.**$* { *; }
-dontwarn com.microblink.**
-keep class com.facetec.zoom.** { *; }
-dontwarn javax.annotation.Nullable
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

Check the Android Studio sample projects to learn the most common use.

## Architecture
You can filter which architecture to use by specifying the abiFilters.

__Note:__ The abiFilters command in the ndk closure affects the Google Play Store filtering.

```
defaultConfig {
	ndk {
		abiFilters armeabi-v7a","arm64-v8a","x86","x86_64"
	}
}
```

The apk can be split based on the architecture if multiple apks should be uploaded to the Google Play Store. Google Play Store manages to deliver the appropriate apk for the device.
```
splits {
	abi {
		enable true
		reset()
		include armeabi-v7a","arm64-v8a","x86","x86_64"
		universalApk false
	}
}
```

__Note:__ You get an *UnsatisfiedLinkError*, if the app and the CPU architecture do not match.

## Configuration

### Localizing labels
Our SDK supports the [default Android localization features](https://developer.android.com/training/basics/supporting-devices/languages.html) for different languages and cultures.
All label texts and button titles in the SDK can be changed and localized by adding the required Strings you want to change in a `strings.xml` file in a `values` directory for the language and culture preference that you want to support. You can check out strings that are modifiable at [.../src/main/res/values/strings-jumio-sdk.xml](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml) within our Sample application.

For our products Fastfill,Netverify & Document Verification we are providing six individual languages for your convenience:
* Chinese (Simplified)
* Dutch
* English
* French
* German
* Spanish

Our SDK supports accessibility features. Visually impaired users can now enable __TalkBack__ or change the __font size__ on their device. The accessibility-strings that are used by TalkBack contain *accessibility_* in their key and can be also modified in the `strings.xml`.

# Get started
- [Integration Netverify & Fastfill SDK](docs/integration_netverify-fastfill.md)
- [Integration Authentication SDK](docs/integration_authentication.md)
- [Integration Document Verification SDK](docs/integration_document-verification.md)
- [Integration BAM Checkout SDK](docs/integration_bam-checkout.md)

# Support

## Previous version
The previous release version 2.15.0 of the Jumio Mobile SDK is supported until 2019-06-05.

In case the support period is expired, no bug fixes are provided anymore (typically fixed in the upcoming versions). The SDK will keep functioning (until further notice).

## Two-factor Authentication
If you want to enable two-factor authentication for your Jumio customer portal please contact us at https://support.jumio.com. Once enabled, users will be guided through the setup upon their first login to obtain a security code using the "Google Authenticator" app.

## Licenses
The software contains third-party open source software. For more information, please see [licenses](https://github.com/Jumio/mobile-sdk-android/tree/master/licenses).

This software is based in part on the work of the Independent JPEG Group.

## Contact
If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. Check it out at: https://support.jumio.com.

## Copyright
&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306

The source code and software available on this website (“Software”) is provided by Jumio Corp. or its affiliated group companies (“Jumio”) "as is” and any express or implied warranties, including, but not limited to, the implied warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall Jumio be liable for any direct, indirect, incidental, special, exemplary, or consequential damages (including but not limited to procurement of substitute goods or services, loss of use, data, profits, or business interruption) however caused and on any theory of liability, whether in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this Software, even if advised of the possibility of such damage.
In any case, your use of this Software is subject to the terms and conditions that apply to your contractual relationship with Jumio. As regards Jumio’s privacy practices, please see our privacy notice available here: [Privacy Policy](https://www.jumio.com/legal-information/privacy-policy/).
