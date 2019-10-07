![BAM Checkout](images/bam_checkout.jpg)

# BAM Checkout SDK for Android
BAM Checkout SDK is a powerful, cutting-edge solution to extract data from your customer´s credit card and/or ID in your mobile application within seconds, including home address. It fits perfectly, and fully automates, every checkout flow to avoid manual input at all which leads to an increased conversion rate.

## Table of Content

- [Release notes](#release-notes)
- [Setup](#setup)
- [Integration](#integration)
- [Initialization](#initialization)
- [Configuration](#configuration)
- [Customization](#customization)
- [SDK Workflow](#sdk-workflow)
- [Card retrieval API](#card-retrieval-api)
- [Two-factor Authentication](#two-factor-authentication)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)

## Release notes
For technical changes, please read our [transition guide](transition-guide_bam-checkout.md) SDK version: 3.3.2

## Setup
The [basic setup](../README.md#basic-setup) is required before continuing with the following setup for Bam-Checkout.

Using the SDK requires an activity declaration in your `AndroidManifest.xml`.

```
<activity
	android:theme="@style/Theme.Bam"
	android:hardwareAccelerated="true"
	android:name="com.jumio.bam.BamActivity"
	android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"/>
```

You can specify your own theme (see [Customization](#customization) chapter). The orientation can be sensor based or locked with the attribute `android:screenOrientation`.

If you want to use offline scanning for BAM Checkout (Credit card scanning), please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com.

## Integration

### Dependencies

|Dependency        | Mandatory           | Description       | Size (Jumio libs only) |
| :---------------------------- |:-------------:|:-----------------|:------------:|
|com.jumio.android:core:3.3.2@aar                    | x | Jumio Core library|	4.09 MB |
|com.jumio.android:bam:3.3.2@aar                     | x | BAM Checkout library | 3.61 MB |
|androidx.appcompat:appcompat:1.0.2            				| x | Android appcompat library| - |
|androidx.room:room-runtime:2.0.0			              | x | Android database object mapping library	| - |
|com.jumio.android:javadoc:3.3.2                     |   | Jumio SDK Javadoc| - |

If an optional module is not linked, the scan method is not available but the library size is reduced.

__Note:__ If you use Netverify and BAM Checkout in your app, add additional dependencies from the Integration chapter from the [Netverify & Fastfill](integration_netverify-fastfill.md) Guide.

Applications implementing the SDK shall not run on rooted devices. Use either the below method or a self-devised check to prevent usage of SDK scanning functionality on rooted devices.
```
BamSDK.isRooted(Context context);
```

Call the method `isSupportedPlatform` to check if the device is supported.
```
BamSDK.isSupportedPlatform();
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

### Card details
To restrict supported card types, pass an ArrayList of `CreditCardTypes` to the `setSupportedCreditCardTypes` method.
```
ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();
creditCardTypes.add(CreditCardType.VISA);
creditCardTypes.add(CreditCardType.MASTER_CARD);
bamSDK.setSupportedCreditCardTypes(creditCardTypes);
```

You can enable the sort code and account number.
The recognition of card holder name, expiry recognition and CVV entry are enabled by default and can be disabled.
The user can edit the recognized expiry date if `setExpiryEditable` is enabled.
```
bamSDK.setCardHolderNameRequired(false);
bamSDK.setSortCodeAndAccountNumberRequired(true)
bamSDK.setExpiryRequired(false);
bamSDK.setCvvRequired(false);
bamSDK.setExpiryEditable(true);
bamSDK.setCardHolderNameEditable(true);
```

### Transaction identifiers
Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).

__Note:__ This is not required for offline scanning.
```
bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");
```

### Offline scanning
If you want to use Fastfill in offline mode please contact Jumio Customer Service at support@jumio.com or https://support.jumio.com. Once this feature is enabled for your account, you can find your offline token in your Jumio customer portal on the "Settings" page under "API credentials".

```
BamSDK.create(rootActivity, YOUROFFLINETOKEN)
```

### Miscellaneous

You can set a short vibration and sound effect to notify the user that the card has been detected.
```
bamSDK.setVibrationEffectEnabled(true);
bamSDK.setSoundEffect(R.raw.yourSoundFile);
```

Use setCameraPosition to configure the default camera (front or back).
```
bamSDK.setCameraPosition(JumioCameraPosition.FRONT);
```

To enable flashlight after SDK is started, use the following method.
```
bamSDK.setEnableFlashOnScanStart(true);
```

To show the unmasked card number during the user journey, disable the following setting.
```
bamSDK.setCardNumberMaskingEnabled(false);
```

You can add custom fields to "Additional info" view (text input field or selection list).
```
bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");

bamSDK.addCustomField("stateId", getString(R.string.state), states, false, getString(R.string.state_reset_value));
```

The SDK is valid until the expiration date is reached. If the Android Certificate Fingerprint does not match with the token anymore or if the token itself is set to an invalid value a `PlatformNotSupportedException` will be thrown.

## Customization

### Customize look and feel
The SDK can be customized to fit your application's look and feel by specifying `Theme.Bam` as a parent style and overriding attributes within this theme. Click on the element `Theme.Bam` in the manifest while holding Ctrl and Android Studio will display the available attributes of the Theme that can be customized.

### Customize theme at runtime
To customize the theme at runtime overwrite the main theme by using the following property.
```
netverifySDK.setCustomTheme(CUSTOMTHEME);
```

## SDK Workflow

### Default scan view

#### Starting the SDK

To show the SDK, call the respective method below within your activity or fragment.

Activity: `bamSDK.start();` <br/>
Fragment: `startActivityForResult(bamSDK.getIntent(), BamSDK.REQUEST_CODE);`

__Note:__ The default request code is 100. To use another code, override the public static variable `BamSDK.REQUEST_CODE` before displaying the SDK.

#### Retrieving information

Implement the standard method `onActivityResult` in your activity for successful scans and user cancellation notifications. Call `bamSDK.destroy()` once you received the result.

You receive a Jumio scan reference for each try, if an Internet connection is available. For offline scans an empty `String` is added to the `Arraylist scanReferences`.

Call `cardInformation.clear()` after processing the card information to make sure no sensitive data remains in the device's memory.

The parameter `EXTRA_ERROR_CODE` contains the user cancellation reason.

__Note:__ The error codes are described [here](#error-codes)

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == BamSDK.REQUEST_CODE) {
		ArrayList<String> scanAttempts = data.getStringArrayListExtra(BamSDK.EXTRA_SCAN_ATTEMPTS);
		if (resultCode == Activity.RESULT_OK) {
			// OBTAIN PARAMETERS HERE
			BamCardInformation cardInformation = data.getParcelableExtra(BamSDK.EXTRA_CARD_INFORMATION);
			// YOURCODE
			// cardInformation.clear();
		}
		else if (resultCode == Activity.RESULT_CANCELED) {
			String errorMessage = data.getStringExtra(BamSDK.EXTRA_ERROR_MESSAGE);
			String errorCode = data.getStringExtra(BamSDK.EXTRA_ERROR_CODE);
			// YOURCODE
		}
		// CLEANUP THE SDK AFTER RECEIVING THE RESULT
	  if (bamSDK != null) {
			bamSDK.destroy();
		 	bamSDK = null;
		}
	}
}
```

### Custom scan view

#### Starting the SDK

To use the custom scan view with a plain card scanning user interface, specify an instance of your class which implements the [BamCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanInterface.html) and provide an instance of the class [BamCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanView.html). You will receive a [BamCustomScanPresenter](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanPresenter.html) object.

Add `yourBamCustomScanView` to your layout and specify desired layout attributes using either
*  a certain width, and height as `wrap_content`
*  or a certain height, and width as `wrap_content`
*  or width and height as `match_parent` (full screen).

Using width or height as `wrap_content`, the `BamCustomScanView` attribute `ratio` needs to be set to any float value between screen width/screen height (e.g. portrait 720/1280 = ~ 0.6) and 4:3 (1.33). If `yourBamCustomScanView` is added to your layout via xml, specify the below namespace to access the custom attribute `yourNameSpace:ratio`.
```
BamCustomScanPresenter bamCustomScanPresenter = bamSDK.start(yourBamCustomScanInterface, yourBamCustomScanView);
```

Upon `onBamCameraAvailable` within `yourBamCustomScanInterface`, you can perform the following actions using the `bamCustomScanPresenter`:
*  Check if front and back camera available
*  Check if front camera used
*  Switch between front and back camera
*  Check if flash available
*  Check if flash enabled
*  Switch flash mode (on or off)
*  Stop card scanning
```
public boolean hasMultipleCameras();
public boolean isCameraFrontFacing();
public void switchCamera();
public boolean hasFlash();
public boolean isFlashOn();
public void toggleFlash();
public void stopScan();
```

Call `onActivityPause` in your activity upon `onPause`.
```
public void onActivityPause();
```

#### Retrieving information
Instead of using the standard method `onActivityResult`, implement the following methods within `yourBamCustomScanInterface` for camera, extraction and error notifications. Call `bamCustomScanPresenter.clearSDK()` and `bamSDK.destroy()` once you received the result.

Upon `onBamError`, you can show the error message and/or call `bamCustomScanPresenter.retryScan()` if retryPossible.

__Note:__ The error codes are described [here](#error-codes)

```
@Override
public void onBamCameraAvailable() {
	// YOURCODE
}

@Override
public void onBamExtractionStarted() {
	// YOURCODE like showing a progress indicator
}

@Override
public void onBamExtractionFinished(BamCardInformation cardInformation, ArrayList<String> scanReferences) {
	// YOURCODE
	// cardInformation.clear();
	// bamCustomScanPresenter.clearSDK();
	// bamSDK.destroy();
}

@Override
public void onBamError(String errorCode, String errorMessage, boolean retryPossible, ArrayList<String> scanAttempts) {
	// YOURCODE like showing the error message and/or calling retry if retryPossible
	// bamCustomScanPresenter.retryScan();
	// bamCustomScanPresenter.clearSDK();
	// bamSDK.destroy();
}
```

### BamCardInformation

|Parameter        			| Type    | Max. length |Description               |
|:---------------------------- 		|:-------------|:-----------------|:-------------|
| cardType         			| CreditCardType | Jumio Core library|  AMERICAN_EXPRESS, CHINA_UNIONPAY, DINERS_CLUB, DISCOVER, JCB, MASTER_CARD or VISA |
| cardNumber       			| char[] | 16 | Full credit card number |
| cardNumberGrouped  		| char[] | 19 | Grouped credit card number |
| cardNumberMasked  		| char[] | 19 | First 6 and last 4 digits of the grouped credit card number, other digits are masked with "X" |
| cardExpiryMonth  			| char[] | 2 | Month card expires if enabled and readable |
| CardExpiryYear  			| char[] | 2 | Year card expires if enabled and readable |
| cardExpiryDate  			| char[] | 5 | Date card expires in the format MM/yy if enabled and readable |
| cardCVV  							| char[] | 4 | Entered CVV if enabled|
| cardHolderName  			| char[] | 100 | Name of the card holder in capital letters if enabled and readable, or as entered if editable|
| cardSortCode  				| char[] | 8 | Sort code in the format xx-xx-xx or xxxxxx if enabled, available and readable|
| cardAccountNumber  		| char[] | 8 | Account number if enabled, available and readable|
| cardSortCodeValid  		| boolean |  | True if sort code valid, otherwise false|
| cardAccountNumberValid| boolean |  | True if account number code valid, otherwise false|

| Method        			| Parameter type | Return type |Description               |
| --------------------|:-------|:-------|:-------------|
| clear         			|   		 |   			| Clear card information    |
| getCustomField      | String | String | Get entered value for added custom field |

### Error codes

| Code        			| Message   | Description     |
| :---------------: |:----------|:----------------|
|B[x][yyyy]| Authentication failed | Secure connection could not be established, retry impossible |
|C[x]0401| Authentication failed | API credentials invalid, retry impossible |
|D[x]0403| Authentication failed | Wrong API credentials used, retry impossible|
|F00000| Scanning not available this time, please contact the app vendor | Resources cannot be loaded, retry impossible |
|G00000| Cancelled by end-user | No error occurred |
|H00000| The camera is currently not available | Camera cannot be initialized, retry impossible |
|I00000| Certificate not valid anymore. Please update your application | End-to-end encryption key not valid anymore, retry impossible |
|L00000| Your card type is not accepted | Retry possible |
|M00000| Background execution is not supported | Cancellation triggered automatically |
|N00000| Your card is expired | Retry possible |

The first letter (B-N) represents the error case. The remaining characters are represented by numbers that contain information helping us understand the problem situation([x][yyyy]). Please always include the whole code when filing an error related issue to our support team.

## Card retrieval API

You can implement RESTful HTTP GET APIs to retrieve credit card image and data for a specific scan. Find the Implementation Guide at the link below.

http://www.jumio.com/implementation-guides/credit-card-retrieval-api/

## Two-factor Authentication

If you want to enable two-factor authentication for your Jumio customer portal please contact us at https://support.jumio.com Once enabled, users will be guided through the setup upon their first login to obtain a security code using the "Google Authenticator" app.
