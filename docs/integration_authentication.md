![Authentication](images/authentication.jpg)

# Authentication SDK for Android
Biometric-based Jumio Authentication establishes the digital identities of your users through the simple act of taking a selfie. Advanced 3D face map technology quickly and securely authenticates users and unlocks their digital identities.

## Table of Contents
- [Release Notes](#release-notes)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Initialization](#initialization)
- [Customization](#customization)
- [SDK Workflow](#sdk-workflow)
- [Custom UI](#custom-ui)
- [Callback](#callback)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)

## Release Notes
Please refer to our [Change Log](changelog.md) for more information. Current SDK version: 3.9.1

For breaking technical changes, please read our [transition guide](transition-guide_authentication.md)


## Setup
The [basic setup](../README.md#basics) is required before continuing with the following setup for Authentication.

Using the SDK requires an activity declaration in your `AndroidManifest.xml`.

```
<activity
            android:name="com.jumio.auth.AuthenticationActivity"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"
            android:hardwareAccelerated="true"
            android:theme="@style/Theme.Authentication"
            android:windowSoftInputMode="adjustResize"/>
```

You can specify your own theme (see chapter [Customization](#customization)). The orientation can be sensor-based or locked with the attribute `android:screenOrientation`. Please note: scanning only works in portrait mode.

__Note:__ Jumio Authentication is not supported on tablets. If it is initialized on a tablet device, a *PlatformNotSupportedException* will be raised.

## Dependencies
Below there is a list of dependices the application will need to work in Android. Some modules are mandatory, others are optinal. If an optional module is __not linked__, some functionalities such as certain methods may not be available, but the library size will be reduced.
```
dependencies {
    // mandatory
    implementation "com.jumio.android:core:3.9.1@aar"       // Jumio Core library
    implementation "com.jumio.android:auth:3.9.1@aar"       // Authentication library

    // Face library
    implementation "com.jumio.android:zoom:3.9.1@aar"       
    implementation "com.facetec:zoom-authentication:8.12.1@aar"

    implementation "androidx.appcompat:appcompat:1.2.0"
    implementation "androidx.room:room-runtime:2.2.6"

    implementation "com.google.android.material:material:1.2.1"
}
```
__Note:__ Version numbers may vary.

### Others

#### Root Detection
Applications implementing the SDK shall not run on rooted devices. Use either the below method or a self-devised check to prevent usage of SDK scanning functionality on rooted devices.
```
AuthenticationSDK.isRooted(Context context);
```

#### Device Supported Check
Call the method `isSupportedPlatform()` to check if the device platform is supported by the SDK.

```
AuthenticationSDK.isSupportedPlatform();
```

## Initialization
Log into your Jumio customer portal. You can find your customer API token and API secret on the __Settings__ page under __API credentials__ tab. To create an instance for the SDK, perform the following call as soon as your activity is initialized.

```
private static String YOURAPITOKEN = ""; 
private static String YOURAPISECRET = "";

AuthenticationSDK authenticationSDK = AuthenticationSDK.create(yourActivity, YOURAPITOKEN, YOURAPISECRET, JumioDataCenter.US);
```
Make sure that your customer API token and API secret are correct, specify an instance
of your activity and provide a reference to identify the scans in your reports (max. 100 characters or `null`). If your customer account is in the EU data center, use `JumioDataCenter.EU` instead. Alternatively, use `JumioDataCenter.SG` for Singapore.

__Note:__ We strongly recommend storing all credentials outside of your app!


## Configuration

### Transaction Identifiers
In order to connect the Authentication transaction to a specific user identity a user reference (max. 100 characters) must be set.

```
authenticationSDK.setUserReference("USERREFERENCE");
```

__Note:__ Transaction identifiers must not contain sensitive data like PII (Personally Identifiable Information) or account login.


### Callback
A callback URL can be specified for individual transactions (for constraints see chapter [Callback URL](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-url)). This setting overrides any callback URL you have set in the Jumio Customer Portal.

```
authenticationSDK.setCallbackUrl("YOURCALLBACKURL");
```

__Note:__ The callback URL must not contain sensitive data like PII (Personally Identifiable Information) or account login.

### Analytics Service
You receive a list of the current DebugSessionID by using getDebugID. This method can be called either after initializing or before dismissing the SDK.

```
authenticationSDK.getDebugID();
```

## Customization
[Jumio Surface](https://jumio.github.io/surface-android/) is a web tool that offers the possibility to apply and visualize, in real-time, all available customization options for Authentication SDK as well as an export feature to import the applied changes straight into your codebase.

Use the tab __Customize SDK__ to check out all the screens and adapt the look and feel of the SDK to your needs.

The tab __XML Output__ visualizes all the colors that can be customized. As visualized in the code there, the SDK can be customized to fit your application's look and feel by specifying `Theme.Authentication` as a parent style and overriding attributes within this theme.

After customizing the SDK, you can copy the code from the theme `CustomAuthenticationTheme` to your Android app `styles.xml` file.

### Customize Look and Feel
There are two possibilities for applying the customized theme that was explained in the previous chapter:
* Customizing theme in AndroidManifest
* Customizing theme at runtime

#### Customizing Theme in AndroidManifest
Apply the `CustomAuthenticationTheme` that you defined earlier by replacing `Theme.Authentication` in the AndroidManifest.xml:
```
<activity
            android:name="com.jumio.auth.AuthenticationActivity"
            android:theme="@style/CustomAuthenticationTheme"
						... />
```

#### Customizing Theme at Runtime
To customize the theme at runtime, overwrite the theme that is used for Authentication in the manifest by calling the following property. Use the resource id of a customized theme that uses Theme.Authentication as parent.

```
authenticationSDK.setCustomTheme(CUSTOMTHEME);
```
__Note:__ Customizations should be applied before the SDK is initialized.

## SDK Workflow

### Starting the SDK
The scan reference of an eligible ID Verification has to be used as the enrollmentTransactionReference
```
authenticationSDK.setEnrollmentTransactionReference(ENROLLMENTTRANSACTIONREFERENCE);
```
In case an Authentication transaction has been created via the facemap server to server API `setAuthenticationTransactionReference` should be used. Therefore `setEnrollmentTransactionReference` should not be called.
```
authenticationSDK.setAuthenticationTransactionReference(AUTHENTICATIONTRANSACTIONREFERENCE);
```
Use the initiate method to preload the SDK. This ensures that the provided enrollment transaction reference or authentication transaction reference is valid and applicable for Authentication. If initialization fails, the SDK can not be started and will throw a `RuntimeException` if `authenticationSDK.start()` or `authenticationSDK.getIntent()` is called.
```
authenticationSDK.initiate(new AuthenticationInitiateCallback() {
	@Override
	public void onAuthenticationInitiateSuccess() {
		// YOURCODE - the SDK can now be started
	}
	@Override
	public void onAuthenticationInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
		// YOURCODE
	}
});
```
To show the SDK, call the respective method below within your activity or fragment.
Activity: `authenticationSDK.start();` <br/>
Fragment: `startActivityForResult(authenticationSDK.getIntent(), AuthenticationSDK.REQUEST_CODE);`

__Note:__ The default request code is 500. To use another code, override the public static variable `AuthenticationSDK.REQUEST_CODE` before displaying the SDK.


### Retrieving Information

Implement the standard `onActivityResult` method in your activity or fragment for successful scans (`Activity.RESULT_OK`) and user cancellation notifications (`Activity.RESULT_CANCELED`). Call `authenticationSDK.destroy()` once you have received the result and you don't need the instance anymore. If you want to perform multiple authentications, you don't need to call delete on the authenticationSDK instance. In that case, please check if the internal resources are deallocated by calling `authenticationSDK.checkDeallocation(<AuthenticationDeallocationCallback>)`. Once this callback is executed, it is safe to start another workflow. This check is optional and should only be called once the SDK has returned a result and another authentication needs to be performed.

```
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == AuthenticationSDK.REQUEST_CODE) {
			if (data == null)
				return;
			if (resultCode == Activity.RESULT_OK) {
				String transactionReference = (data == null) ? "" : data.getStringExtra(AuthenticationSDK.EXTRA_TRANSACTION_REFERENCE);
				AuthenticationResult authenticationResult = (AuthenticationResult) data.getSerializableExtra(AuthenticationSDK.EXTRA_SCAN_DATA);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				String transactionReference = (data == null) ? "" : data.getStringExtra(AuthenticationSDK.EXTRA_TRANSACTION_REFERENCE);
				String errorMessage = data.getStringExtra(AuthenticationSDK.EXTRA_ERROR_MESSAGE);
				String errorCode = data.getStringExtra(AuthenticationSDK.EXTRA_ERROR_CODE);
			}

			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (authenticationSDK != null) {
				authenticationSDK.destroy();
				authenticationSDK.checkDeallocation(deallocationCallback)
				authenticationSDK = null;
			}
	}
}
```

#### Authentication Result

|AuthenticationResult 	|  Description      |
| :---------------------------- |:-----------------|
|SUCCESS|Authentication was successful - user is live and matches his ID/Identity Verification transaction|
|FAILED|Authentication failed - user is not live OR does not match the ID/Identity Verification transaction|

#### Error Codes
List of all **_error codes_** that are available via the `code` property of the AuthenticationError object. he first letter (A-M) represents the error case. The remaining characters are represented by numbers that contain information helping us understand the problem situation([x][yyyy]).

|Code        			| Message  | Description      |
| :--------------:|:---------|:-----------------|
|A[x][yyyy]| We have encountered a network communication problem | Retry possible, user decided to cancel |
|B[x][yyyy]| Authentication failed | Secure connection could not be established, retry impossible |
|C[x]0401| Authentication failed | API credentials invalid, retry impossible |
|E[x]0000| No Internet connection available | Retry possible, user decided to cancel |
|F00000| Scanning not available at this time, please contact the app vendor | Resources cannot be loaded, retry impossible |
|G00000| Cancelled by end-user | No error occurred |
|H00000| The camera is currently not available | Camera cannot be initialized, retry impossible |
|I00000| Certificate not valid anymore. Please update your application | End-to-end encryption key not valid anymore, retry impossible |
|J00000| Transaction already finished | User did not complete SDK journey within session lifetime|
|L00000| Enrollment transaction reference invalid | The provided enrollment transaction reference can not be used for an authentication |
|M00000| The scan could not be processed | An error happened during the processing. The SDK needs to be started again|

__Note:__ Please always include the whole code when filing an error related issue to our support team.

## Custom UI
Authentication can be also implemented as a __custom scan view.__ This means that only the scan view (including the scan overlays) are provided by the SDK.
The handling of the lifecycle, intermediate callbacks, and all other steps necessary to complete a scan have to be handled by the client application that implements the SDK.

__Note:__ Authentication is not supported on tablets. If it is initialized on a tablet device, a *PlatformNotSupportedException* will be raised.

To use the custom scan view with a plain scanning user interface, specify an instance of your class which implements the [AuthenticationCustomSDKInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html). You will receive a [AuthenticationCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html) object.

### AuthenticationCustomScanView Presentation
Add your AuthenticationCustomScanView to your layout and specify desired layout attributes using
* width as match_parent, and __height__ as wrap_content
* or __width__ and __height__ as match_parent (full screen).

Using width as match_parent, the AuthenticationCustomScanView attribute ratio needs to be set to any float value between screen width/screen height (e.g. portrait 720/1280 = ~0.6) and 1. If your AuthenticationCustomScanView is added to your layout via xml, specify the namespace below to access the custom attribute *yourNameSpace:ratio*. Face scans can only be done in portrait orientation with a recommended ratio of 1 or smaller.
```
xmlns:yourNameSpace="http://schemas.android.com/apk/lib/com.jumio.mobile.sdk"
```

The method `onAuthenticationUserConsentRequried` within *yourAuthenticationCustomSDKInterface* is invoked when the end-user’s consent to Jumio’s privacy policy is legally required. [`setUserConsented()`](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#setUserConsented--) needs to be called after the end-user has accepted.

### Start Scanning
Start scanning by providing an instance of the class `AuthenticationCustomScanView` and an instance of your class which implements the [AuthenticationCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html).

### Retrieving Information

#### Result & Error Handling
Instead of using the standard method `onActivityResult()`, implement the following methods within *yourAuthenticationCustomSDKInterface* for successful scans and error notifications:

The method `onAuthenticationFinished(AuthenticationResult authenticationResult, String transactionReference)` has to be implemented to handle data after successful scans.

The method `onAuthenticationError(String errorCode, String errorMessage, boolean retryPossible, String transactionReference)` has to be implemented to handle an unsuccessful scan. You can show the error message and/or call `authenticationCustomSDKController.retry()` if retryPossible.

__Note__: Error codes are listed [here](#error-codes).

#### Clean Up
After handling the result, it is very important to clean up the SDK by calling  `authenticationCustomSDKController.destroy()` and `authenticationSDK.destroy()`. If you want to perform multiple authentications, you don't need to call delete on the authenticationSDK instance. In that case, please check if the internal resources are deallocated by calling `authenticationSDK.checkDeallocation(<AuthenticationDeallocationCallback>)`. Once this callback is executed, it is safe to start another workflow. This check is optional and should only be called once the SDK has returned a result and another authentication needs to be performed.

## Callback
To get information about callbacks, please refer to our [page with server related information](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-for-authentication).
