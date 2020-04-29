![Authentication](images/authentication.jpg)

# Authentication SDK for Android
Biometric-based Jumio Authentication establishes the digital identities of your users through the simple act of taking a selfie. Advanced 3D face map technology quickly and securely authenticates users and unlocks their digital identities.

## Table of Content

- [Release notes](#release-notes)
- [Setup](#setup)
- [Dependencies](#dependencies)
- [Initialization](#initialization)
- [Customization](#customization)
- [SDK Workflow](#sdk-workflow)
- [Custom UI](#custom-ui)
- [Javadoc](https://jumio.github.io/mobile-sdk-android/)

## Release notes
For technical changes, please read our [transition guide](transition-guide_authentication.md) SDK version: 3.6.0

## Setup
The [basic setup](../README.md#basic-setup) is required before continuing with the following setup for Authentication.

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

## Dependencies

|Dependency        | Mandatory           | Description       | Size (Jumio libs only) |
| ---------------------------- |:-------------:|:-----------------|:---------:|
|com.jumio.android:core:3.6.0@aar                    | x | Jumio Core library		                      | 4.09 MB |
|com.jumio.android:auth:3.6.0@aar                      | x | Authentication library 		              | 97.98 KB |
|com.jumio.android:face:3.6.0@aar                     | x | Face library	                            | 100.56 KB |
|com.facetec:zoom-authentication:8.0.11@aar     | x | Zoom face scanning library	              | 9.00 MB  |
|androidx.appcompat:appcompat:1.1.0                   | x | Android appcompat library	                | - |
|androidx.room:room-runtime:2.2.3                     | x | Android database object mapping library	  | - |
|com.google.android.material:material:1.1.0           | x | Android material design library	          | - |
|androidx.localbroadcastmanager:localbroadcastmanager:1.0.0 | x | Android local broadcast manager library | - |
|com.jumio.android:javadoc:3.6.0                     |   | Jumio SDK Javadoc			                    | - |

### Others

#### Root detection
Applications implementing the SDK shall not run on rooted devices. Use either the method below or a self-devised check to prevent usage of SDK scanning functionality on rooted devices.
```
AuthenticationSDK.isRooted(Context context);
```

#### Device supported check
Call the method `isSupportedPlatform` to check if the device platform is supported by the SDK.

```
AuthenticationSDK.isSupportedPlatform();
```

## Initialization
To create an instance for the SDK, perform the following call as soon as your activity is initialized.

```
private static String YOURAPITOKEN = ""; 
private static String YOURAPISECRET = "";

AuthenticationSDK authenticationSDK = AuthenticationSDK.create(yourActivity, YOURAPITOKEN, YOURAPISECRET, JumioDataCenter.US);
```
Make sure that your customer API token and API secret are correct, specify an instance of your activity and provide a reference to identify the transactions in your reports (max. 100 characters or `null`). If your customer account is in the EU data center, use `JumioDataCenter.EU` instead.

__Note:__ When logged into the Jumio Customer Portal, you will find your API token and API secret on the **Settings** page under **API credentials**. We strongly recommend that you store your credentials outside your app.

## Configuration

### Callback
A callback URL can be specified for individual transactions (for constraints see chapter [Callback URL](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-url)). This setting overrides any callback URL you have set in the Jumio Customer Portal.

```
authenticationSDK.setCallbackUrl("YOURCALLBACKURL");
```

### Transaction identifiers
You can set a user reference (max. 100 characters).

__Note:__ The user reference must not contain sensitive data like PII (Personally Identifiable Information) or account login.
```
authenticationSDK.setUserReference("USERREFERENCE");
```

### Analytics Service

You receive a list of the current DebugSessionID by using getDebugID. This method can be called either after initializing or before dismissing the SDK.

```
authenticationSDK.getDebugID();
```

## Customization
[Jumio Surface](https://jumio.github.io/surface-android/) is a web tool that allows you to apply and visualize, in real-time, all available customization options for the Authentication SDK, as well as an export feature to import the applied changes straight into your codebase.

Use the tab __"Customize SDK"__ to check out all the screens and adapt the look and feel of the SDK to your needs.

The tab __"XML Output"__ visualizes all the colors that can be customized. As visualized in the code there, the SDK can be customized to fit your application's look and feel by specifying `Theme.Authentication` as a parent style and overriding attributes within this theme.

After customizing the SDK, you can copy the code from the theme `CustomAuthenticationTheme` to your Android app `styles.xml` file.

### Customize look and feel
There are 2 options for applying the customized theme described in the previous chapter:
* Customizing theme in AndroidManifest
* Customizing theme at runtime


#### Customizing theme in AndroidManifest
Apply the `CustomAuthenticationTheme` that you defined earlier by replacing `Theme.Authentication` in the AndroidManifest.xml:
```
<activity
            android:name="com.jumio.auth.AuthenticationActivity"
            android:theme="@style/CustomAuthenticationTheme"
						... />
```

#### Customizing theme at runtime
To customize the theme at runtime, overwrite the theme that is used for Authentication in the manifest by calling the following property. Use the resource id of a customized theme that uses Theme.Authentication as parent.

```
authenticationSDK.setCustomTheme(CUSTOMTHEME);
```

## SDK Workflow

### Starting the SDK
The scan reference of an eligible Netverify scan has to be used as the enrollmentTransactionReference
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


### Retrieving information

Implement the standard `onActivityResult` method in your activity or fragment for successful scans (`Activity.RESULT_OK`) and user cancellation notifications (`Activity.RESULT_CANCELED`). Call `authenticationSDK.destroy()` once you have received the result and you don't need the instance anymore. If you want to perform multiple authentications, you don't need to call delete on the netverifySDK instance. In that case, please check if the internal resources are deallocated by calling `authenticationSDK.checkDeallocation(<AuthenticationDeallocationCallback>)`. Once this callback is executed, it is safe to start another workflow. This check is optional and should only be called once the SDK has returned a result and another authentication needs to be performed.

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

#### Error codes

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

The first letter (A-M) represents the error case. The remaining characters are represented by numbers that contain information helping us understand the problem situation([x][yyyy]). Please always include the whole code when filing an error related issue to our support team.

## Custom UI
Authentication can be also implemented as a custom scan view. This means that only the scan view (including the scan overlays) are provided by the SDK.
The handling of the lifecycle, intermediate callbacks, and all other steps necessary to complete a scan have to be handled by the client application that implements the SDK.

**Note:** Authentication is not supported on tablets. If it is initialized on a tablet device, a PlatformNotSupportedException will be raised.

To use the custom scan view with a plain scanning user interface, specify an instance of your class which implements the [AuthenticationCustomSDKInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html). You will receive a [AuthenticationCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html) object.


Add your AuthenticationCustomScanView to your layout and specify desired layout attributes using
* width as match_parent, and height as wrap_content
* or width and height as match_parent (full screen).

Using width as match_parent, the AuthenticationCustomScanView attribute ratio needs to be set to any float value between screen width/screen height (e.g. portrait 720/1280 = ~0.6) and 1. If your AuthenticationCustomScanView is added to your layout via xml, specify the namespace below to access the custom attribute *yourNameSpace:ratio*. Face scans can only be done in portrait orientation with a recommended ratio of 1 or smaller.
```
xmlns:yourNameSpace="http://schemas.android.com/apk/lib/com.jumio.mobile.sdk"
```

`onAuthenticationUserConsentRequried` within *yourAuthenticationCustomSDKInterface* is invoked when the end-user’s consent to Jumio’s privacy policy is legally required. [onUserConsented](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomSDKController.html#onUserConsented--) needs to be called after the end-user has accepted

Start scanning by providing an instance of the class `AuthenticationCustomScanView` and an instance of your class which implements the [AuthenticationCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/auth/custom/AuthenticationCustomScanInterface.html).

### Retrieving information

#### Result & Error handling
Instead of using the standard method `onActivityResult`, implement the following methods within *yourAuthenticationCustomSDKInterface* for successful scans and error notifications:

The method `onAuthenticationFinished(AuthenticationResult authenticationResult, String transactionReference)` has to be implemented to handle data after successful scans.

Upon `onAuthenticationError(String errorCode, String errorMessage, boolean retryPossible, String transactionReference)`, you can show the error message and/or call `authenticationCustomSDKController.retry()` if retryPossible.

**Note**: Error codes are listed [here](#error-codes).

#### Clean up
After handling the result, it is very important to clean up the SDK by calling  `authenticationCustomSDKController.destroy()` and `authenticationSDK.destroy()`. If you want to perform multiple authentications, you don't need to call delete on the netverifySDK instance. In that case, please check if the internal resources are deallocated by calling `authenticationSDK.checkDeallocation(<AuthenticationDeallocationCallback>)`. Once this callback is executed, it is safe to start another workflow. This check is optional and should only be called once the SDK has returned a result and another authentication needs to be performed.

## Callback

To get information about callbacks, please read our [page with server related information](https://github.com/Jumio/implementation-guides/blob/master/netverify/callback.md#callback-for-authentication).
