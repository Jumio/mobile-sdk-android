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
- [Callback](#callback)

## Release notes
For changes in the technical area, please read our [transition guide](transition-guide_netverify-fastfill.md).

#### Changes
* Stability improvements
* Minor UI/UX changes

#### Fixes
* Miscellaneous bugfixes

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

If you are using eMRTD scanning, following lines are needed:

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
```

If you want to use offline scanning for Fastfill please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com.

## Dependencies

If an optional module is __not linked__, the __scan method is not available__ but the library size is reduced.

|Dependency        | Mandatory           | Description       |
| ---------------------------- |:-------------:|:-----------------|
| com.jumio.android:core:2.8.0@aar                   | x | Jumio Core library|
| com.jumio.android:nv:2.8.0@aar                     | x | Netverify library |
|com.android.support:appcompat-v7:25.3.1             | x | Android native library|
|com.android.support:support-v4:25.3.1               | x | Android native library|
|com.google.android.gms:play-services-vision:11.0.0  | x | Barcode Scanning |
|com.jumio.android:nv-liveness:2.8.0@aar 		| x | Face-Liveness library|
|com.android.support:design:25.3.1                   |   | Android native library|
|com.jumio.android:javadoc:2.8.0                     |   | Jumio SDK Javadoc|
|com.jumio.android:nv-barcode:2.8.0@aar              |   | US / CAN Barcode Scanning|
|com.jumio.android:nv-barcode-vision:2.8.0@aar 			 |   | US / CAN Barcode Scanning Alternative (reduced size) |
|com.jumio.android:nv-mrz:2.8.0@aar             		 |   | MRZ scanning|
|com.jumio.android:nv-nfc:2.8.0@aar              		 |   | eMRTD Scanning|
|com.madgag.spongycastle:prov:1.56.0.0             	 |   | eMRTD Scanning|
|net.sf.scuba:scuba-sc-android:0.0.12             	 |   | eMRTD Scanning|
|com.jumio.android:nv-ocr:2.8.0@aar             		 |   | Template Matcher|


__Note:__ If you use Netverify and BAM Checkout in your app, add the following dependency:

```
compile "com.jumio.android:bam:2.8.0@aar"
```

Applications implementing the SDK shall not run on rooted devices. Use either the below method or a self-devised check to prevent usage of SDK scanning functionality on rooted devices.
```
NetverifySDK.isRooted(Context context);
```

Call the method `isSupportedPlatform` to check if the device is supported.

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
Make sure that your merchant API token and API secret are correct, specify an instance
of your activity and provide a reference to identify the scans in your reports (max. 100 characters or `null`). If your merchant account is in the EU data center, use `JumioDataCenter.EU` instead.

__Note:__ Log into your Jumio merchant backend, and you can find your merchant API token and API secret on the "Settings" page under "API credentials". We strongly recommend you to store credentials outside your app.

## Configuration

### ID verification

By default, the SDK is used in Fastfill mode which means it is limited to data extraction only. No verification of the ID is performed.

Enable ID verification to receive a verification status and verified data positions (see [Callback for Netverify](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-for-netverify)). A callback URL can be specified for individual transactions (constraints see chapter [Callback URL](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-url)). Ensure that your merchant account is allowed to use this feature.

__Note:__ Not possible for accounts configured as Fastfill only.
```
netverifySDK.setRequireVerification(true);
netverifySDK.setCallbackUrl("YOURCALLBACKURL");
```
You can enable face match during the ID verification for a specific transaction (if it is enabled for your account).
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

__Note:__ The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
```
netverifySDK.setCustomerId("CUSTOMERID");
```
You can also set an additional information parameter (max. 255 characters).

__Note:__ The additional information should not contain sensitive data like PII (Personally Identifiable Information) or account login.
```
netverifySDK.setAdditionalInformation("ADDITIONALINFORMATION");
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

If you want to use Fastfill in offline mode please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com. Once they have enabled this feature for your account, you can find your offline token in your Jumio merchant backend on the "Settings" page under "API credentials".

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
[Jumio Surface](https://jumio.github.io/surface-android/) is a web tool that offers the possibility to apply and visualize, in real-time, all available customization options for Netverify / Fastfill SDK as well as an export feature to import the applied changes straight into the your codebase.

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
	public void onNetverifyInitiateError(int errorCode, int errorDetail, String errorMessage, boolean retryPossible) {
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
			// int errorCode = data.getIntExtra(NetverifySDK.EXTRA_ERROR_CODE, 0);
			// String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
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

Class **_NetverifyDocumentData:_**

|Parameter | Type  	| Max. length    | Description     |
|:-------------------|:----------- 	|:-------------|:-----------------|
|selectedCountry|	String|	3|	[ISO 3166-1 alpha-3](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code as provided or selected|
|selectedDocumentType|	NVDocumentType |	|	PASSPORT, DRIVER_LICENSE, IDENTITY_CARD or VISA as provided or selected|
|idNumber|	String|	100	|Identification number of the document|
|personalNumber|	String|	14|	Personal number of the document|
|issuingDate|	Date|	|	Date of issue|
|expiryDate| Date|	|	Date of expiry|
|issuingCountry|	String|	3|	Country of issue as ([ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code|
|lastName|	String|	100	|Last name of the customer|
|firstName|	String|	100	|First name of the customer|
|middleName|	String|	100	|Middle name of the customer|
|dob|	Date|		|Date of birth|
|gender|	NVGender|		| Gender M or F|
|originatingCountry|	String|	3|	Country of origin as ([ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) country code|
|addressLine|	String|	64	|Street name|
|city|	String|	64	|City|
|subdivision|	String|	3|	Last three characters of [ISO 3166-2:US](http://en.wikipedia.org/wiki/ISO_3166-2:US) state code	|
|postCode|	String|	15|	Postal code	|
|mrzData|	NetverifyMrzData|		|MRZ data, see table below|
|optionalData1|	String|	50|	Optional field of MRZ line 1|
|optionalData2|	String|	50	|Optional field of MRZ line 2|
|placeOfBirth|	String|	255	|Place of Birth	|
|extractionMethod|	NVExtractionMethod| |Extraction method used during scanning (MRZ, OCR, BARCODE, BARCODE_OCR or NONE) |
|emrtdStatus|	EMRTDStatus	| |	Verification status of an eMRTD scan VERIFIED (eMRTD scanned and authenticated), DENIED (eMRTD scanned and not authenticated) or NOT_AVAILABLE (no NFC on device or eMRTD feature disabled), NOT_PERFORMED (NFC disabled on device)|

Class **_NetverifyMrzData_**

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

Class **_Error codes_**

|Code        			| Message  | Description      |
| :--------------:|:---------|:-----------------|
|100<br/>110<br/>130<br/>140<br/>150<br/>160| We have encountered a network communication problem | Retry possible, user decided to cancel |
|200<br/>210<br/>220| Authentication failed | API credentials invalid, retry impossible |
|230| No Internet connection available | Retry possible, user decided to cancel |
|240| Scanning not available this time, please contact the app vendor | Resources cannot be loaded, retry impossible |
|250| Cancelled by end-user | No error occurred |
|260| The camera is currently not available | Camera cannot be initialized, retry impossible |
|280| Certificate not valid anymore. Please update your application | End-to-end encryption key not valid anymore, retry impossible |
|290| Transaction already finished | User did not complete SDK journey within token lifetime|


## Callback

To get information about callbacks, Netverify Retrieval API, Netverify Delete API and Global Netverify settings and more, please read our [page with server related information](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md).

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
