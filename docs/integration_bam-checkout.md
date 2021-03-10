![BAM Checkout](images/bam_checkout.jpg)

# BAM Checkout SDK For Android
BAM Checkout SDK is a powerful, cutting-edge solution to extract data from your customer's credit card and/or ID in your mobile application within seconds, including home address. It fits perfectly into and fully automates every checkout flow to avoid manual input, which leads to an increased conversion rate.

## Table of Contents
- [Release Notes](#release-notes)
- [Setup](#setup)
- [Integration](#integration)
- [Initialization](#initialization)
- [Configuration](#configuration)
- [Customization](#customization)
- [SDK Workflow](#sdk-workflow)
- [Card Retrieval API](#card-retrieval-api)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)

## Release Notes
Please refer to our [Change Log](changelog.md) for more information. Current SDK version: 3.9.1

For breaking technical changes, please read our [transition guide](transition-guide_bam-checkout.md)

## Setup
The [basic setup](../README.md#basics) is required before continuing with the following setup for BAM-Checkout.

Using the SDK requires an activity declaration in your `AndroidManifest.xml`.

```
<activity
	android:theme="@style/Theme.Bam"
	android:hardwareAccelerated="true"
	android:name="com.jumio.bam.BamActivity"
	android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"/>
```

You can specify your own theme (see [Customization](#customization)). The orientation can be sensor based or locked with the attribute `android:screenOrientation`.

If you want to use offline scanning for BAM Checkout (credit card scanning), contact Jumio Support at support@jumio.com or https://support.jumio.com.

## Integration

### Dependencies
Following is the list of dependicies the application will need for Android. Some modules are mandatory, others are optional. If an optional module is __not linked__, some functionalities such as certain methods may not be available, but the library size will be reduced.
```
dependencies {
    // mandatory
    implementation "com.jumio.android:core:3.9.1@aar"       // Jumio Core library
    implementation "com.jumio.android:bam:3.9.1@aar"        // BAM checkout library

    implementation "androidx.appcompat:appcompat:1.2.0"
    implementation "androidx.room:room-runtime:2.2.6"
    implementation "com.google.android.material:material:1.2.1"
}
```
__Note:__ If you use ID Verification together with BAM Checkout in your app, you must also add the [ID Verification dependencies](integration_id-verification-fastfill.md#dependencies).

#### Root Detection
Applications implementing the SDK should not run on rooted devices. Use this method or a self-devised check to prevent usage of SDK scanning functionality on rooted devices:
```
BamSDK.isRooted(Context context);
```

#### Device Supported Check
Call the method `isSupportedPlatform` to check whether the device is supported.
```
BamSDK.isSupportedPlatform();
```

## Initialization
To create an instance for the SDK, perform the following call as soon as your activity is initialized.
```
private static String YOURAPITOKEN = ""; 
private static String YOURAPISECRET = "";

BamSDK bamSDK = BamSDK.create(yourActivity, YOURAPITOKEN, YOURAPISECRET, JumioDataCenter.US);
```
Make sure that your customer API token and API secret are correct, specify an instance of your activity and provide a reference to identify the scans in your reports (max. 100 characters or `null`). If your customer account is in the EU data center, use `JumioDataCenter.EU` instead.

__Note:__ Log into your Jumio customer portal, and you can find your customer API token and API secret on the "Settings" page under "API credentials". We strongly recommend you to store credentials outside your app.

## Configuration

### Card Details
To restrict supported card types, pass an ArrayList of `CreditCardTypes` to the `setSupportedCreditCardTypes` method.
```
ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();
creditCardTypes.add(CreditCardType.VISA);
creditCardTypes.add(CreditCardType.MASTER_CARD);
bamSDK.setSupportedCreditCardTypes(creditCardTypes);
```

You can enable the sort code and account number.
The recognition of card holder name, expiry recognition, and CVV entry are enabled by default and can be disabled.
The user can edit the recognized expiry date if `setExpiryEditable` is enabled.
```
bamSDK.setCardHolderNameRequired(false);
bamSDK.setSortCodeAndAccountNumberRequired(true)
bamSDK.setExpiryRequired(false);
bamSDK.setCvvRequired(false);
bamSDK.setExpiryEditable(true);
bamSDK.setCardHolderNameEditable(true);
```

### Transaction Identifiers
Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).

__Note:__ This is not required for offline scanning.
```
bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");
```

__Note:__ Transaction identifiers must not contain sensitive data like Personally Identifiable Information (PPI) or account login credentials.

### Offline Scanning
If you want to use the SDK in offline mode, please contact Jumio Support at support@jumio.com or https://support.jumio.com. Once this feature is enabled for your account, you can find your offline token in your Jumio customer portal on the __Settings__ page under __API credentials.__

```
BamSDK.create(rootActivity, YOUROFFLINETOKEN)
```

### Miscellaneous
To set a short vibration and sound effect, to notify the user that the card has been detected, use:
```
bamSDK.setVibrationEffectEnabled(true);
bamSDK.setSoundEffect(R.raw.yourSoundFile);
```

Use setCameraPosition to configure the default camera (front or back).
```
bamSDK.setCameraPosition(JumioCameraPosition.FRONT);
```

To enable the flashlight after the SDK is started, use the following method:
```
bamSDK.setEnableFlashOnScanStart(true);
```

To show the unmasked card number during the user journey, disable the following setting:
```
bamSDK.setCardNumberMaskingEnabled(false);
```

You can add custom fields to "Additional info" view (text input field or selection list).
```
bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");

bamSDK.addCustomField("stateId", getString(R.string.state), states, false, getString(R.string.state_reset_value));
```

The SDK is valid until the expiration date is reached. If the Android Certificate Fingerprint does not match with the token anymore, or if the token itself is set to an invalid value a `PlatformNotSupportedException` will be thrown.

## Customization
This section describes the two methods for customizing the look and feel.

### Customize Look and Feel
There are two possibilities for applying the customized theme that was explained in the previous chapter:
* Customizing theme in AndroidManifest
* Customizing theme at runtime

#### Customizing Theme in AndroidManifest
Define the `CustomBamTheme` and then apply it by replacing `Theme.Bam` in `AndroidManifest.xml`:
```
<activity
android:name="com.jumio.bam.BamActivity"
            android:theme="@style/CustomBamTheme"
                        ... />
```

#### Customizing the Theme at Runtime
To customize the theme at runtime, overwrite the theme that is used for Bam in the manifest by calling the following property. Use the resource ID of a customized theme that uses `Theme.Bam` as parent.

```
bamSDK.setCustomTheme(CUSTOMTHEME);
```
__Note:__ Customizations should be applied before the SDK is initialized.

## SDK Workflow
This section describes how to start the SDK and retrieve information in both the [default scan view](#default-scan-view) and the [custom scan view](#custom-scan-view).

### Default Scan View

#### Starting The SDK
To show the SDK, call the respective method below within your activity or fragment.

Activity: `bamSDK.start();` <br/>
Fragment: `startActivityForResult(bamSDK.getIntent(), BamSDK.REQUEST_CODE);`

__Note:__ The default request code is 100. To use another code, override the public static variable `BamSDK.REQUEST_CODE` before displaying the SDK.

#### Retrieving Information
Implement the standard method `onActivityResult` in your activity for successful scans and user cancellation notifications. Call `bamSDK.destroy()` once you receive the result.

If an Internet connection is available, you will receive a Jumio scan reference for each try. For offline scans, an empty `String` is added to the `Arraylist scanReferences`.

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

### Custom Scan View

#### Starting The SDK
To use the custom scan view with a plain card scanning user interface, specify an instance of your class that implements the [BamCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanInterface.html) and provide an instance of the class [BamCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanView.html). You will receive a [BamCustomScanPresenter](https://jumio.github.io/mobile-sdk-android/com/jumio/bam/custom/BamCustomScanPresenter.html) object.

Add `yourBamCustomScanView` to your layout and specify the desired layout attributes using one of the following approaches:
*  Set a specific width but set height to `wrap_content`
*  Set a specific height but set width to `wrap_content`
*  Set both width and height to `match_parent` (full screen).

Setting width or height to `wrap_content`, the `BamCustomScanView` attribute `ratio` needs to be set to any float value between screen width/screen height (e.g., portrait 720/1280 = ~ 0.6) and 4:3 (1.33). If `yourBamCustomScanView` is added to your layout via XML, specify the below namespace to access the custom attribute `yourNameSpace:ratio`.
```
BamCustomScanPresenter bamCustomScanPresenter = bamSDK.start(yourBamCustomScanInterface, yourBamCustomScanView);
```

Upon `onBamCameraAvailable` within `yourBamCustomScanInterface`, you can perform the following actions using the `bamCustomScanPresenter`:
*  Check if front and back camera are available
*  Check if front camera is used
*  Switch between front and back camera
*  Check if flash is available
*  Check if flash is enabled
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

#### Retrieving Information
Instead of using the standard method `onActivityResult`, implement the following methods within `yourBamCustomScanInterface` for camera, extraction and error notifications. Call `bamCustomScanPresenter.clearSDK()` and `bamSDK.destroy()` once you receive the result.

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
| cardNumberMasked  		| char[] | 19 | First 6 and last 4 digits of the grouped credit card number, remaining digits are masked with "X" |
| cardExpiryMonth  			| char[] | 2 | Month card expires if enabled and readable |
| CardExpiryYear  			| char[] | 2 | Year card expires if enabled and readable |
| cardExpiryDate  			| char[] | 5 | Date card expires in the format MM/yy if enabled and readable |
| cardCVV  							| char[] | 4 | Entered CVV if enabled|
| cardHolderName  			| char[] | 100 | Name of the card holder in capital letters if enabled and readable, or as entered if editable|
| cardSortCode  				| char[] | 8 | Sort code in the format xx-xx-xx or xxxxxx if enabled, available, and readable|
| cardAccountNumber  		| char[] | 8 | Account number if enabled, available and readable|
| cardSortCodeValid  		| boolean |  | True if sort code valid, otherwise false|
| cardAccountNumberValid| boolean |  | True if account number code valid, otherwise false|

| Method        			| Parameter type | Return type |Description               |
| --------------------|:-------|:-------|:-------------|
| clear         			|   		 |   			| Clear card information    |
| getCustomField      | String | String | Get entered value for added custom field |

### Error Codes
List of all **_error codes_** that are available via the `code` property of the Error object. The first letter (B-N) represents the error case. The remaining characters are represented by numbers that contain information helping us understand the problem situation([x][yyyy]).

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

__Note:__ Always include the whole code when filing an error related issue with our support team.

## Card Retrieval API
You can implement RESTful HTTP GET APIs to retrieve credit card image and data for a specific scan. For more information, see the [Credit Card Retrieval API Guide](http://www.jumio.com/implementation-guides/credit-card-retrieval-api/).
