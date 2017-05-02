![Jumio](docs/images/jumio_feature_graphic.png)

# Table of Content
- [Release notes](#release-notes)
- [Basic Setup](#basic-setup)
- [Get started](#get-started)
- [Support](#support)

# Release notes

Applies to all products.

* Changed provider for SDK frameworks, sample code and documentation to Github

# Basic Setup

## General Requirements
The requirements for the SDK are:
*	Android 4.1 (API level 16) or higher
*	ARMv7 processor with Neon, ARM64-v8a
*	Internet connection

## Permissions
Required permissions are linked automatically by the SDK.

The following permissions are optional:
```
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-feature android:name="android.hardware.camera" android:required="false"/>
```

**Note:** On devices running Android Marshmallow (6.0) you need to acquire `android.permissions.CAMERA` dynamically before initializing the SDK.

Use `getRequiredPermissions` to get a list of all required permissions.

```
public static String[] getRequiredPermissions();
```

## Proguard
If you are using Proguard, add the following lines in its configuration.

```
-keep class com.jumio.** { *; }
-keep class jumio.** { *; }
```

## Integration
Use the SDK in your application by including the Maven repository with the following `build.gradle` configuration in Android Studio:

```
repositories {
	maven {
		url 'http://mobile-sdk.jumio.com'
	}
}
```

Check the Android Studio sample projects to learn the most common use.

## Architecture
You can filter which architecture to use by specifying the abiFilters.

__Note:__ The abiFilters command in the ndk closure affects the Google Play Store filtering.

```
defaultConfig {
	ndk {
		abiFilters "armeabi","armeabi-v7a","arm64-v8a","mips","mips64","x86","x86_64"
	}
}
```

The apk can be splitted based on the architecture if multiple apks should be uploaded to the Google Play Store. Google Play Store manages to deliver the appropriate apk for the device.
```
splits {
	abi {
		enable true
		reset()
		include "armeabi","armeabi-v7a","arm64-v8a","mips","mips64","x86","x86_64"
		universalApk false
	}
}
```

__Note:__ You get an *UnsatisfiedLinkError*, if the app and the CPU architecture do not match.

## Configuration

### Localizing labels
Our SDK supports the [default Android localization features](https://developer.android.com/training/basics/supporting-devices/languages.html) for different languages and cultures.
All label texts and button titles in the SDK can be changed and localized by adding the required Strings you want to change in a `strings.xml` file in a `values` directory for the language and culture preference that you want to support. You can check out strings that are modifiable at `../res/values/strings.xml` within our Sample application.

With accessibility support, visually impaired users can now enable __TalkBack__ or change the __font size__ on their device. The accessibility-strings that are used by TalkBack contain *accessibility_* in their key and can be also modified in the `strings.xml`.

# Get started
- [Integration Netverify & Fastfill](docs/integration_netverify-fastfill.md)
- [Integration Netverify Multi Document](docs/integration_multi-document.md)
- [Integration BAM Checkout](docs/integration_bam-checkout.md)

# Support

## Previous version
The previous release version 2.6.0 of the Jumio Mobile SDK is supported until 2017-08-02.

In case the support period is expired, no bug fixes are provided anymore (typically fixed in the upcoming versions). The SDK will keep functioning (until further notice).

## Two-factor Authentication
If you want to enable two-factor authentication for your Jumio merchant backend please contact us at https://support.jumio.com. Once enabled, users will be guided through the setup upon their first login to obtain a security code using the "Google Authenticator" app.

## Contact
If you have any questions regarding our implementation guide please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com. The Jumio online helpdesk contains a wealth of information regarding our service including demo videos, product descriptions, FAQs and other things that may help to get you started with Jumio. Check it out at: https://support.jumio.com.

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
