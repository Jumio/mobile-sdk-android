# Known Issues

## Removing Zoom library (3.7.2)
 When removing the Zoom library, it is necessary to remove the face library too, as well as adding a dummy drawable `face_ic_clear` to prevent build issues. This will be fixed in the next version.

 __Please remove:__
```
implementation "com.facetec:zoom-authentication:8.12.1@aar"     // Zoom face scanning library  
implementation "com.jumio.android:face:3.7.2@aar"               // Face library
```

## Custom theme issues

### Updated screens for Zoom face scanning (3.7.2)
As of __version 3.7.2__, the screens for face scanning have been updated. There are three new screens available: An __upfront help view__, the actual __Zoom scan view__ for face scanning like before, and an __error / help view__ in case of problems:

![upfront help](images/images_zoom_update/upfront_help.png)  ![scan screen](images/images_zoom_update/scan_screen.png)  ![error default](images/images_zoom_update/error_help_default_blurred.jpg)

The background color of the screens can be customized using the parameter `face_scanOverlayBackground`, pictured __black__ above. All foreground elements, text and the help screen overlay can be customized using `face_scanOverlayOval`, pictured __white__ above, and button backgrounds as well as progress text can be customized using `face_scanOverlayProgress`, pictured __green__ above.
```
<item name="face_scanOverlayBackground">@color/jumio_black</item> // background of all Zoom screens
<item name="face_scanOverlayOval">@color/jumio_white</item> // all foreground elements, texts, overlay on help screen
<item name="face_scanOverlayProgress">@color/jumio_primary</item> // button background on upfront help and as a secondary color to "face_scanOverlayOval", otherwise primary color is used by default
```

The parameters `face_scanOverlayFeedbackText`, as well as `face_scanOverlayFeedbackBackground` remain __unchanged.__ The same goes for all `face_helpXXXXX` attributes.

As of 7.3.2, the attribute `face_helpProgressString` is deprecated and can be removed.

### Custom theme is not working     
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

### Scan overlay is not displayed  
Make sure all necessary style attributes have been added to your custom theme specified in the `style.xml` file. In case of issues with scan overlay, all relevant attributes start with `scanOverlay` and `face_scanOverlay`.

An overview of all style attributes [can be found here](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/styles.xml)

## Fallback and Manual capturing

The method [`isFallbackAvailable()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#isFallbackAvailable--) determines if a fallback for the current scan mode is available and returns a boolean. If the method returns true, the available fallback scan mode will have to be started with the method [`startFallback()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#startFallback--).

The method [`showShutterButton()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#showShutterButton--) determines if a shutter button needs to be shown because the image has to be taken manually and returns a boolean. If the method returns true, you will have to display your own shutter button and call the method [`takePicture()`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#takePicture--) once it is clicked.

__Note:__ Please note that the method `showShutterButton()` does neither create nor display the actual shutter button!

"Manual capturing" simply refers to the user being able to manually take a picture. "Fallback" refers to an alternative scan mode the SDK can resort to if possible, in case there is an issue during the original scanning process. The fallback scan mode might be manual capturing in some cases, but not all.

## Country missing from the country list

Countries with documents that are [MRZ](integration_glossary.md)-capable (which is the case for most passports) might not be available if the necessary MRZ dependency is missing. The dependency in question is:
```
implementation "com.jumio.android:nv-mrz:3.7.1@aar"
```
A complete list of all dependencies [can be found here.](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_id-verification-fastfill.md#dependencies)

__Note:__ Version numbers may vary.

## Language issues

[`Jumio Android Localization`](../README.md#language-localization) supports the default Android localization features for a number different languages. Any language changes within the SDK or separate language support during runtime (meaning the SDK language differs from the overall device languages) are not possible.

All modifiable strings can be modified can be found [over here](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml)
To localize something, add the strings you want to change to a `strings.xml` file in a `values`  directory for the language and culture preference that you want to support.

Please refer to the [FAQ](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#language-localization) for more detailed information.

## Sample app crashes at start

### Intent filter and credentials
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

### Kotlin integration  
Since the Jumio SDK is using Android instant apps, it is necessary to implement Kotlin into your project, even if the project itself if written in Java.
