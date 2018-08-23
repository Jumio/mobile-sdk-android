![Fastfill & Netverify](images/netverify.png)

# Fastfill & Netverify SDK for Android
Netverify & Fastfill SDK offers scanning and authentication of governmental issued IDs.

## Table of Content

- [Release notes](#release-notes)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Initialization](#integration)
- [Configuration](#configuration)
- [Customization](#customization)
- [SDK Workflow](#sdk-workflow)
- [Custom UI](#custom-ui)
- [Callback](#callback)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)

## Release notes
For technical changes, please read our [transition guide](transition-guide_netverify-fastfill.md) SDK version: 2.12.1

## Setup
The [basic setup](../README.md#basic-setup) is required before continuing with the following setup for Netverify.

Using the SDK requires an activity declaration in your `AndroidManifest.xml`.

```
<activity
	android:theme="@style/Theme.Netverify"
	android:hardwareAccelerated="true"
	android:name="com.jumio.nv.NetverifyActivity"
	android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"/>
```

You can specify your own theme (see chapter [Customization](#customization)). The orientation can be sensor based or locked with the attribute `android:screenOrientation`.

If you are using eMRTD scanning, the following lines are needed in your `proguard-rules.pro` file:

```
-keep class net.sf.scuba.smartcards.IsoDepCardService {*;}
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.spongycastle.** {*;}
-keep class org.ejbca.** {*;}

-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn org.ejbca.**
-dontwarn org.spongycastle.**
-dontwarn org.jmrtd.PassportService
-dontwarn net.sf.scuba.**
```

If you want to use offline scanning for Fastfill please contact your Jumio Customer Success Manager.

## Dependencies

If an optional module is __not linked__, the __scan method is not available__ but the library size is reduced.
The [Sample app](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/) apk size with the products Netverify, BAM and Document Verification included is currently __20.69 MB__.

|Dependency        | Mandatory           | Description       | Size (Jumio libs only) |
| ---------------------------- |:-------------:|:-----------------|:---------:|
| com.jumio.android:core:2.12.1@aar                   | x | Jumio Core library		| 4.57 MB |
| com.jumio.android:nv:2.12.1@aar                     | x | Netverify library 		| 538.59 KB |
|com.android.support:appcompat-v7:27.1.1             | x | Android native library	| - |
|com.android.support:support-v4:27.1.1               | x | Android native library	| - |
|com.android.support:cardview-v7:27.1.1              | x | Android cardview library (Netverify only)	| - |
|com.google.android.gms:play-services-vision:12.0.0  |   | Barcode Scanning 			| - |
|com.jumio.android:nv-liveness:2.12.1@aar 		         | x | Face-Liveness library	| 4.32 MB |
|com.android.support:design:27.1.1                   |   | Android native library	| - |
|com.jumio.android:javadoc:2.12.1                     |   | Jumio SDK Javadoc			| - |
|com.jumio.android:nv-barcode:2.12.1@aar              |   | US / CAN Barcode Scanning | 3.46 MB |
|com.jumio.android:nv-barcode-vision:2.12.1@aar 			 |   | US / CAN Barcode Scanning Alternative (reduced size) | 36.72 KB |
|com.jumio.android:nv-mrz:2.12.1@aar             		 |   | MRZ scanning 					| 2.21 MB |
|com.jumio.android:nv-nfc:2.12.1@aar              		 |   | eMRTD Scanning 				| 887.77 KB |
|com.madgag.spongycastle:prov:1.58.0.0             	 |   | eMRTD Scanning 				| - |
|net.sf.scuba:scuba-sc-android:0.0.16             	 |   | eMRTD Scanning 				| - |
|com.jumio.android:nv-ocr:2.12.1@aar             		 |   | Template Matcher 			| 1.58 MB |

### Google Mobile Vision

#### Dependency conflicts
If the dependencies `com.jumio.android:nv-liveness` and `com.jumio.android:nv-barcode-vision` are both used in the application, the following lines have to be added to the application tag in the AndroidManifest.xml to avoid merge issues (see [AndroidManifest.xml](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/AndroidManifest.xml) in Sample app):
```
<meta-data
			android:name="com.google.android.gms.vision.DEPENDENCIES"
			android:value="barcode, face"
			tools:replace="android:value"/>
```

#### Operationality
If the Google Mobile Vision API is not operational on a device, a manual fallback will be used for the face workflow.
The operationality of the Google Mobile Vision API can be checked with the following SDK method (see [NetverifyFragment](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/NetverifyFragment.java)  in Sample app):
```
GoogleVisionStatus NetverifySDK.isMobileVisionOperational(Activity activity, int requestCode);
```
This method returns an enum [GoogleVisionStatus](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.GoogleVisionStatus.html) which can have the following 3 values:
* __OPERATIONAL__: API is up-to-date and can be used
* __NOT_OPERATIONAL__: API is not available
* __DIALOG_PENDING__: API is available but an user-resolvable error occured. The system dialog for the resolvable error is displayed (see [Google API reference](https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability))

In case of __DIALOG_PENDING__, the `requestCode` provided in the method above can used to react to the result of the dialog in the method `onActivityResult()` as follows (see [MainActivity](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/MainActivity.java)  in Sample app)):
```
@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NetverifyFragment.GOOGLE_VISION_REQUEST_CODE) {
			// Handle the system dialog result - try to initialize the SDK again if the error was resolved
		}
	}
```

### Others

#### Netverify usage with BAM
If you use Netverify and BAM Checkout in your app, add the following dependency:

```
implementation "com.jumio.android:bam:2.12.1@aar"
```

#### Root detection
Applications implementing the SDK shall not run on rooted devices. Use either the below method or a self-devised check to prevent usage of SDK scanning functionality on rooted devices.
```
NetverifySDK.isRooted(Context context);
```

#### Device supported check
Call the method `isSupportedPlatform` to check if the device platform is supported by the SDK.

```
NetverifySDK.isSupportedPlatform();
```

## Initialization
To create an instance for the SDK, perform the following call as soon as your activity is initialized.

```
private static String YOURAPITOKEN = ""; 
private static String YOURAPISECRET = "";

NetverifySDK netverifySDK = NetverifySDK.create(yourActivity, YOURAPITOKEN, YOURAPISECRET, JumioDataCenter.US);
```
Make sure that your customer API token and API secret are correct, specify an instance
of your activity and provide a reference to identify the scans in your reports (max. 100 characters or `null`). If your customer account is in the EU data center, use `JumioDataCenter.EU` instead.

__Note:__ Log into your Jumio customer portal, and you can find your customer API token and API secret on the "Settings" page under "API credentials". We strongly recommend you to store credentials outside your app.

## Configuration

### ID verification

By default, the SDK is used in Fastfill mode which means it is limited to data extraction only. No verification of the ID is performed.

Enable ID verification to receive a verification status and verified data positions (see [Callback for Netverify](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-for-netverify)). A callback URL can be specified for individual transactions (constraints see chapter [Callback URL](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-url)). Ensure that your customer account is allowed to use this feature.

__Note:__ Not possible for accounts configured as Fastfill only.
```
netverifySDK.setRequireVerification(true);
netverifySDK.setCallbackUrl("YOURCALLBACKURL");
```
You can enable Identity Verification during the ID verification for a specific transaction (if it is enabled for your account).
```
netverifySDK.setRequireFaceMatch(true);
```

### Preselection

You can specify issuing country  ([ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code), ID type(s) and/or document variant to skip their selection during the scanning process.<br />
__Note:__ Fastfill does not support paper IDs, except German ID cards.
```
netverifySDK.setPreselectedCountry("AUT");
netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);

ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
documentTypes.add(NVDocumentType.PASSPORT);
documentTypes.add(NVDocumentType.DRIVER_LICENSE);
netverifySDK.setPreselectedDocumentTypes(documentTypes);
```

### Transaction identifiers

The merchant scan reference allows you to identify the scan (max. 100 characters).

__Note:__ Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
```
netverifySDK.setMerchantScanReference("YOURSCANREFERENCE");
```
Use the following property to identify the scan in your reports (max. 100 characters).
```
netverifySDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");
```
You can also set a customer identifier (max. 100 characters).

__Note:__ The customer ID must not contain sensitive data like PII (Personally Identifiable Information) or account login.
```
netverifySDK.setCustomerId("CUSTOMERID");
```

### eMRTD

Use `setEnableEMRTD` to read the NFC chip of an eMRTD.
```
netverifySDK.setEnableEMRTD (true);
```
__Note:__ Not available for Fastfill as it is a Netverify feature.

### Analytics Service

Use the following setting to explicitly send debug information to Jumio.
```
netverifySDK.setSendDebugInfoToJumio(true);
```

__Note:__ Only set this property to true if you are asked by Jumio Customer Service.

You receive a list of the current DebugSessionID by using getDebugID. This method can be called either after initializing or before dismissing the SDK.

```
netverifySDK.getDebugID();
```

### Offline scanning

If you want to use Fastfill in offline mode please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com. Once this feature is enabled for your account, you can find your offline token in your Jumio customer portal on the "Settings" page under "API credentials".

```
netverifySDK.create(rootActivity, YOUROFFLINETOKEN, COUNTRYCODE)
```

__Note:__ COUNTRYCODE is an optional parameter and can also be passed as `null`. In this case no country is preselected in the SDK.

Possible countries: [ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code

Use the nv-barcode-vision library instead of the play-services-vision libary and add the following meta-data tags to your `AndroidManifest.xml`:

```
<meta-data
			android:name="com.google.android.gms.version"
			android:value="@integer/google_play_services_version" />
<meta-data
			android:name="com.google.android.gms.vision.DEPENDENCIES"
			android:value="barcode, face"
			tools:replace="android:value"/>
```

### Miscellaneous

In case Fastfill is used (requireVerification=NO), data extraction can be limited to be executed on device only by enabling `setDataExtractionOnMobileOnly`
```
netverifySDK.setDataExtractionOnMobileOnly(true);
```

Use `setCameraPosition` to configure the default camera (front or back).
```
netverifySDK.setCameraPosition(JumioCameraPosition.FRONT);
```

## Customization

### Customization tool
[Jumio Surface](https://jumio.github.io/surface-android/) is a web tool that offers the possibility to apply and visualize, in real-time, all available customization options for Netverify / Fastfill SDK as well as an export feature to import the applied changes straight into your codebase.

### Customize look and feel

The SDK can be customized to fit your application's look and feel by specifying `Theme.Netverify` as a parent style and overriding attributes within this theme. Click on the element `Theme.Netveriy` in the manifest while holding Ctrl and Android Studio will display the available attributes of the Theme that can be customized.

### Customizing theme at runtime

To customize the theme at runtime, overwrite the theme that is used for Netverify in the manifest by calling the following property. Use the resource id of a customized theme that uses Theme.Netverify as parent.

```
netverifySDK.setCustomTheme(CUSTOMTHEME);
```

## SDK Workflow

### Starting the SDK

Use the initiate method to preload the SDK and avoid the loading spinner after the SDK start.
```
netverifySDK.initiate(new NetverifyInitiateCallback() {
	@Override
	public void onNetverifyInitiateSuccess() {
		// YOURCODE
	}
	@Override
	public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
		// YOURCODE
	}
});
```
To show the SDK, call the respective method below within your activity or fragment.

Activity: `netverifySDK.start();` <br/>
Fragment: `startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);`

__Note:__ The default request code is 200. To use another code, override the public static variable `NetverifySDK.REQUEST_CODE` before displaying the SDK.


### Retrieving information (Fastfill)

Implement the standard `onActivityResult` method in your activity or fragment for successful scans (`Activity.RESULT_OK`) and user cancellation notifications (`Activity.RESULT_CANCELED`). Call `netverifySDK.destroy()` once you received the result.

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == NetverifySDK.REQUEST_CODE) {
		if (resultCode == Activity.RESULT_OK) {
			// OBTAIN PARAMETERS HERE
			// YOURCODE
		} else if (resultCode == Activity.RESULT_CANCELED) {
			// String scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
			// String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
			// String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);
			// YOURCODE
		}
		// CLEANUP THE SDK AFTER RECEIVING THE RESULT
		// if (netverifySDK != null) {
		// 	netverifySDK.destroy();
		// 	netverifySDK = null;
		// }
	}
}
```

#### NetverifyDocumentData

|Parameter | Type  	| Max. length    | Description     |
|:-------------------|:----------- 	|:-------------|:-----------------|
|selectedCountry|	String|	3|	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code as provided or selected|
|selectedDocumentType|	NVDocumentType |	|	PASSPORT, DRIVER_LICENSE, IDENTITY_CARD or VISA as provided or selected|
|idNumber|	String|	100	|Identification number of the document|
|personalNumber|	String|	14|	Personal number of the document|
|issuingDate|	Date|	|	Date of issue|
|expiryDate| Date|	|	Date of expiry|
|issuingCountry|	String|	3|	Country of issue as [ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code|
|lastName|	String|	100	|Last name of the customer|
|firstName|	String|	100	|First name of the customer|
|dob|	Date|		|Date of birth|
|gender|	NVGender|		| Gender M, F or X|
|originatingCountry|	String|	3|	Country of origin as [ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code|
|addressLine|	String|	64	|Street name|
|city|	String|	64	|City|
|subdivision|	String|	3|	Last three characters of [ISO 3166-2:US](https://en.wikipedia.org/wiki/ISO_3166-2:US) or [ISO 3166-2:CA](https://en.wikipedia.org/wiki/ISO_3166-2:CA) subdivision code	|
|postCode|	String|	15|	Postal code	|
|mrzData|	NetverifyMrzData|		|MRZ data, see table below|
|optionalData1|	String|	50|	Optional field of MRZ line 1|
|optionalData2|	String|	50	|Optional field of MRZ line 2|
|placeOfBirth|	String|	255	|Place of Birth	|
|extractionMethod|	NVExtractionMethod| |Extraction method used during scanning (MRZ, OCR, BARCODE, BARCODE_OCR or NONE) |
|emrtdStatus|	EMRTDStatus	| |	Verification status of an eMRTD scan VERIFIED (eMRTD scanned and authenticated), DENIED (eMRTD scanned and not authenticated) or NOT_AVAILABLE (no NFC on device or eMRTD feature disabled), NOT_PERFORMED (NFC disabled on device)|

#### NetverifyMrzData

|Parameter  |Type 	| Max. length | Description      |
|:----------|:------|:------------|:-----------------|
|format|	NVMRZFormat|		|
|line1|	String|	50|	MRZ line 1	|
|line2|	String| 50|	MRZ line 2	|
|line3|	String|	50|	MRZ line 3	|
|idNumberValid|	boolean| |	True if ID number check digit is valid, otherwise false	|
|dobValid	|boolean | |True if date of birth check digit is valid, otherwise false	|
|expiryDateValid|	boolean| |		True if date of expiry check digit is valid or not available, otherwise false|
|personalNumberValid	|boolean| |		True if personal number check digit is valid or not available, otherwise false|
|compositeValid|	boolean| |		True if composite check digit is valid, otherwise false	|

#### Error codes

|Code        			| Message  | Description      |
| :--------------:|:---------|:-----------------|
|A10000| We have encountered a network communication problem | Retry possible, user decided to cancel |
|B10000| Authentication failed | Secure connection could not be established, retry impossible |
|C10401| Authentication failed | API credentials invalid, retry impossible |
|D10403| Authentication failed | Wrong API credentials used, retry impossible|
|E20000| No Internet connection available | Retry possible, user decided to cancel |
|F00000| Scanning not available this time, please contact the app vendor | Resources cannot be loaded, retry impossible |
|G00000| Cancelled by end-user | No error occurred |
|H00000| The camera is currently not available | Camera cannot be initialized, retry impossible |
|I00000| Certificate not valid anymore. Please update your application | End-to-end encryption key not valid anymore, retry impossible |
|J00000| Transaction already finished | User did not complete SDK journey within session lifetime|

The first letter (A-J) represents the error case. The remaining characters are represented by numbers that contain information helping us understand the problem situation. Please always include the whole code when filing an error related issue to our support team.

## Custom UI

Netverify can be also implemented as a custom scan view. This means that only the scan view (including the scan overlays) are provided by the SDK.
The handling of the lifecycle, document selection, readability confirmation, intermediate callbacks, and all other steps necessary to complete a scan have to be handled by the client application that implements the SDK.

To use the custom scan view with a plain scanning user interface, specify an instance of your class which implements the [NetverifyCustomSDKInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html). You will receive a [NetverifyCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html) object.

```
NetverifyCustomSDKController netverifyCustomSDKController = sdk.start(yourNetverifyCustomSDKInterface);
```

Upon `onNetverifyCountriesReceived` within *yourNetverifyCustomSDKInterface*, specify country, document type, and document variant to receive all relevant scan parts for the specific document.

```
@Override
public void onNetverifyCountriesReceived(HashMap<String, NetverifyCountry>
countryList, String userCountryCode) {
    // YOURCODE
    // List<ScanSide> netverifyScanSides = netverifyCustomSDKController.setDocumentConfiguration(netverifyCountries.get("USA"), NVDocumentType.PASSPORT, NVDocumentVariant.PLASTIC);
}
```

**[NetverifyCountry](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCountry.html)** methods:
```
public String getIsoCode();
public Set<NVDocumentType> getDocumentTypes();
public Set<NVDocumentVariant> getDocumentVariants(NVDocumentType documentType);
```

**[NVDocumentType](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/data/document/NVDocumentType.html)** values: `PASSPORT`, `ID_CARD`, `DRIVER_LICENSE`

**[NVDocumentVariant](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/data/document/NVDocumentVariant.html)** values: `PAPER`, `PLASTIC`

**[NetverifyScanMode](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyScanMode.html)** values: `MRZ`, `BARCODE`, `FACE`, `MANUAL`, `OCR_CARD`, `OCR_TEMPLATE`

**[NVScanSide](https://jumio.github.io/mobile-sdk-android/com/jumio/core/data/document/ScanSide.html)** values: `FRONT`, `BACK`, `FACE`

After `onNetverifyResourcesLoaded` within *yourNetverifyCustomSDKInterface*, start scanning by providing a ScanSide from the list, instances of the class `NetverifyCustomScanView` and `NetverifyCustomConfirmationView`, and an instance of your class which implements the `NetverifyCustomScanInterface`. You will receive a `NetverifyCustomScanViewController` object.


Add yourNetverifyCustomScanView to your layout and specify desired layout attributes using
* a certain width, and height as wrap_content
* or a certain height, and width as wrap_content
* or width and height as match_parent (full screen).


Using width or height as wrap_content, the NetverifyCustomScanView attribute ratio needs to be set to any float value between screen width/screen height (e.g. portrait 720/1280 = ~0.6) and 4:3 (1.33). If your NetverifyCustomScanView is added to your layout via xml, specify the namespace below to access the custom attribute *yourNameSpace:ratio*. Face scans should only be done in portrait orientation with a recommended ratio of 0.7 or smaller.
```
xmlns:yourNameSpace="http://schemas.android.com/apk/lib/com.jumio.mobile.sdk"
```

Upon `onNetverifyCameraAvailable` within *yourNetverifyCustomScanInterface*, you can perform the following actions using the netverifyCustomScanViewController:

* Get the active scan mode
* Get the help text for the active scan mode
* Check if front and back camera available
* Check if front camera used
* Switch between front and back camera
* Check if flash available
* Check if flash enabled
* Switch flash mode (on or off)
* Check if scan fallback is possible
* Switch from default scan mode (MRZ or bar code) to fallback - remember to get the new scan mode and help text with the available getters after that
* Stop/Retry card scanning
* Pause/Resume extraction - the camera preview keeps running in the meantime


Call `showShutterButton` to determine if the image will be taken manually. If so, display your shutter button and call `takePicture()` once clicked.

To handle the activity lifecycle correctly, call `pause` and `resume` from the `NetverifyCustomSDKController` and `NetverifyCustomScanPresenter` if currently active.

Implement the following methods within
* NetverifyCustomScanInterface for camera, extraction, confirmation view
and special notifications.
* NetverifyCustomSDKInterface for general SDK notifications.

Upon `onNetverifyPresentConfirmationView`, you can hide the scan view and show the confirmation view (asking user to confirm the image), retry, and/or confirm the scan.

**Note:** *yourNetverifyCustomScanView* can be added to your layout by specifying any desired layout attributes.

Upon `onNetverifyNoUSAddressFound` after a Fastfill US Driver License back side scan in barcode mode, you can start a front side scan in OCR mode (fallback) to receive the address (if needed) and/or confirm the scan.

Upon `onNetverifyFaceOnBackside` after a backside scan of an ID or Driver License, the scanning will restart automatically to let the user recapture the backside, as a face was detected, indicating that the user has scanned the frontside in error.

Upon `onNetverifyFaceInLandscape`, notify the user that he should rotate the device to portrait orientation to continue with face scanning.

Upon `onNetverifyShowLegalAdvice`, it is necessary to display the provided legal advice to the user.

Upon `onNetverifyDisplayBlurHint`, it is necessary to  notify the user that the image is blurry and therefore can't be taken. (Manual image capturing only)

Upon `onNetverifyScanForPartFinished`, call `netverifyCustomScanViewController.destroy()` to release all resources before scanning the next part, until all parts are scanned. Once completed, call `netverifyCustomSDKController.finish()` to finish the scan.

### Retrieving information

#### Result & Error handling
Instead of using the standard method `onActivityResult`, implement the following methods within *yourNetverifyCustomSDKInterface* for successful scans and error notifications:

The method `onNetverifyFinished(NetverifyDocumentData documentData, String scanReference)` has to be implemented to handle data after successful scans.

Upon `onNetverifyError(String errorCode, String errorMessage, boolean retryPossible, String scanReference)`, you can show the error message and/or call `netverifyCustomSDKController.retry()` if retryPossible.

**Note**: Error codes are listed [here](#error-codes).

#### Clean up
After handling the result, it is very important to clean up the SDK by calling  `netverifyCustomSDKController.destroy()` and `netverifySDK.destroy()`.

## Callback

To get information about callbacks, Netverify Retrieval API, Netverify Delete API, Global Netverify settings, and more, please read our [page with server related information](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md).

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
