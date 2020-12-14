# Known Issues

## Table of Contents
- [SDK Crashes Trying to Display Animations (Android Version 5 and Lower)](#sdk-crashes-trying-to-display-animations-(android-version-4-and-lower))
- [Custom Theme Issues](#custom-theme-issues)
  - [Custom Theme Is Not Working](#custom-theme-is-not-working)
  - [Scan Overlay Is Not Displayed](#scan-overlay-is-not-displayed)
- [Fallback and Manual Capturing](#fallback-and-manual-capturing)
- [Country Missing from the Country List](#country-missing-from-the-country-list)
- [Language Issues](#language-issues)
- [Sample App Crashes at Start](#sample-app-crashes-at-start)
  - [Intent Filter and Credentials](#intent-filter-and-credentials)
  - [Kotlin Integration](#kotlin-integration)

## SDK Crashes Trying to Display Animations (Android Version 5 and Lower)
Running the SDK on API Level 21/Android Version 5 ("Lollipop") or lower, the application might crash when trying to display Jumio animations. In this case it is necessary to add the line `AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)` to the `onCreate()` method of your application or `Activity`, ideally before `setContentView()` is called.

__Note:__ Refer to [required for vector drawable compat handling](https://stackoverflow.com/a/37864531/1297835) for further information.

## Custom Theme Issues

### Custom Theme Is Not Working     
Any customized theme needs to be defined in a `styles.xml` file and has to inherit from the parent theme `Theme.Netverify`.
```
<style name="AppTheme.NetverifyCustom" parent="Theme.Netverify">
  <item name="colorPrimary">@color/colorPrimary</item>
  ...
</style>

```
The actual name of the customized theme is arbitrary and can be chosen at will.

Any customized theme needs to be added to the `AndroidManifest.xml` file by replacing the initial `Theme.Netverify`.  
```
<activity
  android:name="com.jumio.nv.NetverifyActivity"
  android:theme="@style/CustomNetverifyTheme"
  ...
<activity/>
```

### Scan Overlay Is Not Displayed  
Make sure all necessary style attributes have been added to your custom theme specified in the `style.xml` file. In case of issues with scan overlay, all relevant attributes start with `scanOverlay` and `face_scanOverlay`.

An overview of all style attributes [can be found here](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/styles.xml)

## Fallback and Manual Capturing

The method [`isFallbackAvailable()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#isFallbackAvailable--) determines if a fallback for the current scan mode is available and returns a boolean. If the method returns true, the available fallback scan mode will have to be started with the method [`startFallback()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#startFallback--).

The method [`showShutterButton()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#showShutterButton--) determines if a shutter button needs to be shown because the image has to be taken manually and returns a boolean. If the method returns true, you will have to display your own shutter button and call the method [`takePicture()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#takePicture--) once it is clicked.

__Note:__ Please note that the method `showShutterButton()` does neither create nor display the actual shutter button!

"Manual capturing" simply refers to the user being able to manually take a picture. "Fallback" refers to an alternative scan mode the SDK can resort to if possible, in case there is an issue during the original scanning process. The fallback scan mode might be manual capturing in some cases, but not all.

## Country Missing from the Country List

Countries with documents that are [MRZ](integration_glossary.md)-capable (which is the case for most passports) might not be available if the necessary MRZ dependency is missing. The dependency in question is:
```
implementation "com.jumio.android:nv-mrz:3.7.1@aar"
```
A complete list of all dependencies [can be found here.](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_id-verification-fastfill.md#dependencies)

__Note:__ Version numbers may vary.

## Language Localization Issues

[`Jumio Android Localization`](../README.md#language-localization) supports the default Android localization features for a number different languages. Any language changes within the SDK or separate language support during runtime (meaning the SDK language differs from the overall device languages) are not possible.

All modifiable strings can be modified can be found [over here](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml)
To localize something, add the strings you want to change to a `strings.xml` file in a `values`  directory for the language and culture preference that you want to support.

Please refer to the [FAQ](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#language-localization) for more detailed information.

## Sample App Crashes at Start

### Intent Filter and Credentials
The sample project contains two identical packages: One in Kotlin, one in Java. Please note that the `intent filter` in the sample project is set to Kotlin by default.
```
		<activity
			android:name="com.jumio.sample.kotlin.MainActivity"
			... >
			<intent-filter>
				<action android:name="android.intent.action.MAIN"/>
				<category android:name="android.intent.category.LAUNCHER"/>
			</intent-filter>
		</activity>
```

Make sure to set the intent filter for the correct activity in the `AndroidManifest.xml` file and add your API credentials (API token and API secret) to the right package.

### Kotlin Integration  
Since the Jumio SDK is partly written in Kotlin, it is necessary to add the Kotlin standard library dependency to your project, even if the project itself if written in Java.
