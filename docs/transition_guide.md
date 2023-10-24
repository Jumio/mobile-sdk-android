![Header Graphic](images/jumio_feature_graphic.jpg)

# Transition Guide for Android SDK
This section covers all technical changes that should be considered when updating from previous versions, including, but not exclusively: API breaking changes or new functionality in the public API, major dependency changes, attribute changes, deprecation notices.

⚠️&nbsp;&nbsp;When updating your SDK version, __all__ changes/updates made in in the meantime have to be taken into account and applied if necessary.     
__Example:__ If you're updating from SDK version __3.7.2__ to __3.9.2__, the changes outlined in __3.8.0, 3.9.0__ and __3.9.1__ are __still relevant__.

## 4.8.1
No backward incompatible changes

## 4.8.0
No backward incompatible changes

## 4.7.1
No backward incompatible changes

## 4.7.0
#### Public API Changes
* `rawBarcodeData` has been removed from [`JumioIDResult`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.result/-jumio-i-d-result/index.html)
* `LEGAL_HINT` has been removed from [`JumioScanUpdate`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-update/index.html)
* `giveDataDogConsent` has been removed from [`JumioSDK`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk/-jumio-s-d-k/-companion/index.html)

#### Dependency Updates
* Removed MRZ dependency: ~~`implementation "com.jumio.android:mrz:4.6.0"`~~
* Removed Linefinder dependency: ~~`implementation "com.jumio.android:linefinder:4.6.0"`~~
* Removed Barcode dependency: ~~`implementation "com.jumio.android:barcode:4.6.0"`~~
* Datadog update: ~~`"com.datadoghq:dd-sdk-android:1.19.3"`~~ is replaced by `"com.datadoghq:dd-sdk-android-rum:2.0.0"` - If Datadog is used in a dynamic feature module please have a look at [this known issue](known_issues.md#datadog-in-dynamic-feature-modules).

#### Custom UI Changes
* The platform check has been moved from the [`JumioSDK`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk/-jumio-s-d-k/-companion/index.html) constructor to the [`JumioController`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.controller/-jumio-controller/index.html) constructor. In case the platform is not supported there will be a non-retryable F000001 [`JumioError`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.error/-jumio-error/index.html) delivered in [`JumioControllerInterface$onError`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.interfaces/-jumio-controller-interface/on-error.html) instead of a [`PlatformNotSupportedException`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.exceptions/-platform-not-supported-exception/index.html) being thrown. Please also make sure to check [`isSupportedPlatform`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_guide.md#device-supported-check) before using the SDK.

#### Localization Changes
* SDK string translations for Brazilian Portuguese (pt-rBR) have been added

#### Customization Changes
* Customization attribute ~~`<item name="jumio_face_animation_background">`~~ has been removed

#### Documentation Changes
* Functions `persist` and `stop` in [`JumioController`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.controller/-jumio-controller/index.html) need to be called independently from `isComplete` as long as the workflow is not yet finished or canceled.

## 4.6.1
No backward incompatible changes

## 4.6.0
#### Public API Changes
* `JUMIO_LIVENESS` has been added to [`JumioScanMode`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-mode/index.html)
* `MOVE_FACE_CLOSER` has been added to [`JumioScanUpdate`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-update/index.html)
* `FACE_TOO_CLOSE` has been added to [`JumioScanUpdate`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-update/index.html)

#### Customization Changes
* A new customization theme `<item name="jumio_face_animation_customization">@style/CustomFaceHelp</item>` has been added to help customize the newly added Jumio Liveness solution. This style includes the following attributes:
  * `<item name="jumio_face_animation_foreground">`
  * `<item name="jumio_face_animation_background">`


* The following customization attributes have been added to `@style/CustomOverlay` theme:
  * `<item name="jumio_scanOverlay">`
  * `<item name="jumio_scanOverlay_livenessStrokeAnimation">`
  * `<item name="jumio_scanOverlay_livenessStrokeAnimationCompleted>`      


* The following customization attributes have been removed from `@style/CustomIproov` theme:
  * ~~`<item name="iproov_animation_foreground">`~~
  * ~~`<item name="iproov_animation_background">`~~


* See also: [Jumio sample `styles.xml`](../sample/JumioMobileSample/src/main/res/values/styles.xml)

#### Dependency Updates
* NEW Liveness dependency: `implementation "com.jumio.android:liveness:4.6.0"`

## 4.5.1
No backward incompatible changes

## 4.5.0
#### Public API Changes
* ~~`onPause`~~ has been removed from [JumioScanPart](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.scanpart/-jumio-scan-part/index.html)

* [`JumioError.code`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.error/-jumio-error/index.html) format updated from `[A][x][yyyy]` to `[A][xx][yyyy]`

* Property [`countries`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.credentials/-jumio-i-d-credential/countries.html) of `JumioIDCredential` has been deprecated. Instead the following new property and functions have been added:
    * [`JumioIDCredential.supportedCountries`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.credentials/-jumio-i-d-credential/supported-countries.html)
    * [`JumioIDCredential.getPhysicalDocumentsForCountry(countryCode:)`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.credentials/-jumio-i-d-credential/get-physical-documents-for-country.html)
    * [`JumioIDCredential.getDigitalDocumentsForCountry(countryCode:)`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.credentials/-jumio-i-d-credential/get-digital-documents-for-country.html)

* [`JumioDeepLinkHandler`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.util/-jumio-deep-link-handler/index.html) has been added

* [`JumioPhysicalDocument`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.document/-jumio-physical-document/index.html) and [`JumioDigitalDocument`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.document/-jumio-digital-document/index.html) have been added

* [`JumioDocument`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.document/-jumio-document/index.html) type has changed to interface. (Original `JumioDocument` class has been replaced by `JumioPhysicalDocument`)

* `DIGITAL` has been added in [`JumioCredentialPart`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-credential-part/index.html)

* `DIGITAL_IDENTITY_VIEW` and `THIRD_PARTY_VERIFICATION` have been added in [`JumioScanStep`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-step/index.html)

* [`JumioRetryReasonDigitalIdentity`](https://jumio.github.io/mobile-sdk-android/jumio-digital-identity/com.jumio.sdk.retry/-jumio-retry-reason-digital-identity/index.html) has been added

* [`JumioConsentItem`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.consent/-jumio-consent-item/index.html) class and [`JumioConsentType`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-consent-type/index.html) enum have been added

* [`onInitialized()`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.interfaces/-jumio-controller-interface/on-initialized.html) callback has been changed from ~~`onInitialized(credentials: List<JumioCredentialInfo>, policyUrl: String?)`~~ to [`onInitialized(credentials: List<JumioCredentialInfo>, consentItems: List<JumioConsentItems>?)`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.interfaces/-jumio-controller-interface/on-initialized.html)
  * Please refer to the [Consent Handling section](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_guide.md#consent-handling) in our integration guide for more details.

#### Localization Keys
The following keys have been added to `strings.xml`:
  * jumio_idtype_di
  * jumio_di_vendor_selection_title
  * jumio_di_retry_unknown
  * jumio_di_retry_third_party_verification_error
  * jumio_di_retry_service_error
  * jumio_di_retry_expired
  * jumio_di_back_to_document_selection

#### Dependency Updates
* IProov update: ~~`"com.iproov.sdk:iproov:8.0.3"`~~ is replaced by `"com.iproov.sdk:iproov:8.3.1"`

## 4.4.2
No backward incompatible changes

## 4.4.1
No backward incompatible changes

## 4.4.0
#### Public API Changes
* `credentialParts` property of [`JumioCredential` class](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.credentials/-jumio-credential/credential-parts.html) has been changed from `ArrayList<JumioCredentialPart>` to `List<JumioCredentialPart>`
* [`JumioConfirmationHandler`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.handler/-jumio-confirmation-handler/index.html) has been added. Attach a [JumioScanPart](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.scanpart/-jumio-scan-part/index.html) to this class to retrieve all accepted images and render them to [`JumioConfirmationView`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.views/-jumio-confirmation-view/index.html) objects for confirmation.
* [`JumioRejectHandler`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.handler/-jumio-reject-handler/index.html) has been added. Attach a [JumioScanPart](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.scanpart/-jumio-scan-part/index.html) to this class to retrieve all rejected images and render them to [`JumioRejectView`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.views/-jumio-reject-view/index.html) objects for retaking.
* Functions in [`JumioConfirmationView`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.views/-jumio-confirmation-view/index.html) have been moved to [`JumioConfirmationHandler`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.handler/-jumio-confirmation-handler/index.html).
* Functions in [`JumioRejectView`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.views/-jumio-reject-view/index.html) have been moved to [`JumioRejectHandler`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.handler/-jumio-reject-handler/index.html)
* `MULTIPART` has been added in [`JumioCredentialPart`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-credential-part/index.html) as a new Autocapture scan part: Instead of having a single scan part for all parts of a document (front, back), there is now a single `MULTIPART` scan part that combines the two. Within this scan part all needed parts of a document are captured at once.
* `NEXT_PART` has been added in [`JumioScanStep`](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-scan-step/index.html): This scan step shows that the previous part has been captured and the next one can be started (e.g. frontside has been captured, now switch to the backside of the document)

#### Customization Updates
* Attributes changed and added to [`Iproov.Customization` theme](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/styles.xml#L95)

#### Dependency Updates
* IProov update: ~~`"com.iproov.sdk:iproov:7.5.0"`~~ is replaced by `"com.iproov.sdk:iproov:8.0.3"`

## 4.3.0
#### Minimum SDK Version Changes
* minSdkVersion has been increased to 21. The SDK can still be integrated in Apps that support lower minSdkVersions - check if the [platform is supported](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk/-jumio-s-d-k/-companion/is-supported-platform.html) before initializing the JumioSDK, otherwise it will throw a [PlatformNotSupportedException](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.exceptions/-platform-not-supported-exception/index.html).

#### Dependency Updates
* IProov update: ~~`"com.iproov.sdk:iproov:7.2.0"`~~ is replaced by `"com.iproov.sdk:iproov:7.5.0"`

#### Public API Changes
* Document Verification is now supported. Please check the [Integration Guide](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_guide.md#jumio-document-credential) for more information.

#### Public API Changes
* ~~`JumioCameraPosition`~~ from package `com.jumio.sdk.enums` in `com.jumio.sdk:core` is replaced by `JumioCameraFacing`
* `JumioAcquireMode` has been added to package `com.jumio.sdk.enums` in `com.jumio.sdk:core`, containing fields `FILE` and `CAMERA`
* [`JumioDataCredential` class](integration_guide.md/#jumio-data-credential) has been added for handling of Device Fingerprinting
* [`JumioDocumentCredential` class](integration_guide.md/#jumio-document-credential) has been added for Document Verification handling

## 4.2.0
#### Public API Changes
* In [JumioControllerInterface](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.interfaces/-jumio-controller-interface/index.html) the signature of function `onInitialized` has been changed. Parameter `credentials` has been changed from `ArrayList<JumioCredentialInfo>` to `List<JumioCredentialInfo>`
* In [JumioResult](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.result/-jumio-result/index.html) field `credentialInfos` has been changed from `ArrayList<JumioCredentialInfo>?` to `List<JumioCredentialInfo>?`
* `JumioScanSide` from package `com.jumio.sdk.enums` in `com.jumio.sdk:core` has been renamed to [JumioCredentialPart](https://jumio.github.io/mobile-sdk-android/jumio-core/com.jumio.sdk.enums/-jumio-credential-part/index.html)

#### Dependency Updates
* NEW Autocapture dependency (Beta): `implementation "com.jumio.android:docfinder:4.2.0"`

#### Customization Updates
* Boolean `iproov_floating_prompt_enabled` has been added to [`Iproov.Customization` theme](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/styles.xml#L84)
* Color attribute ~`iproov_footerTextColor` has been replaced with `iproov_promptTextColor` in [`Iproov.Customization` theme](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/styles.xml#L84)

#### Deprecation Notice  
⚠️&nbsp;&nbsp;SDK 4.2.0 will be the last SDK version supporting Android 4.4 (API level 19). All subsequent SDK versions will require at least Android 5.0 "Lollipop" (API level 21).

## 4.1.1
No backward incompatible changes

## 4.1.0
#### Dependency Updates
* NEW Datadog dependency (optional): `implementation "com.jumio.android:datadog:4.1.0"`

* IProov update: ~~`"com.iproov.sdk:iproov:7.0.3"`~~ is replace by `"com.iproov.sdk:iproov:7.2.0"`

#### Customization Updates
* Dark mode is now available. DefaultUI will switch automatically if system settings of the user device change.
* Dark mode can also be customized by creating a custom theme, utilizing `values-night` in the resources directory.

#### Instant Feedback Reject Reasons
Added Instant Feedback functionality to give more granular user feedback with new reject reasons:
* BLACK_WHITE_COPY
* COLOR_PHOTOCOPY
* DIGITAL_COPY
* NOT_READABLE
* NO_DOC
* MISSING_BACK
* MISSING_FRONT
* BLURRY
* MISSING_PART_DOC
* DAMAGED_DOCUMENT
* HIDDEN_PART_DOC
* GLARE

## 4.0.0   
#### Authentication
ℹ️&nbsp;&nbsp;__As of version 4.0.0 and onward, the SDK can only be used in combination with Jumio KYX or Jumio API v3. API v2 as well as using API token and secret to authenticate against the SDK will no longer be compatible.__

#### Dependency Updates
* The repository declaration for ~~`jcenter()`~~ is replaced with `mavenCentral()` as [JFrog will be shutting down JCenter](https://blog.gradle.org/jcenter-shutdown)

* Additionally to that, the repository declaration `gradlePluginPortal()` was added to mitigate the gradle build plugin dependency not being migrated to `mavenCentral()` yet.

* All AndroidX dependencies are now declared in the `.pom` files and resolved transitively by Gradle. The following AndroidX dependencies are used in the SDK, but __do not__ have to be declared manually in the `build.gradle` anymore:
  * `"androidx.appcompat:appcompat:1.3.0"`
  * `"com.google.android.material:material:1.4.0"`
  * `"androidx.constraintlayout:constraintlayout:2.1.1"`
  * `"androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"`
  * `"androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"`
  * `"androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1"`
  * `"androidx.lifecycle:lifecycle-extensions:2.2.0"`
  * `"androidx.recyclerview:recyclerview:1.2.1"`
  * `"androidx.fragment:fragment-ktx:1.3.6"`
  * `"androidx.navigation:navigation-ui-ktx:2.3.5"`     


* The Jumio liveness dependency `"com.iproov.sdk:iproov:7.0.3"` is referenced as a transitive dependency within the iProov module and does not have to be added manually to the `build.gradle` anymore.

* `kotlin-parcelize` and `kotlinx-serialization` plugins, as well as the following dependencies have been removed:
  * `org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0`
  * `org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0`

#### Initialization Updates
* ~~`apiToken`~~ and ~~`apiSecret`~~ are replaced by one-time `sdk.token`

#### Default UI Updates
As of SDK version 4.0.0, a lot of SDK parameters that previously could be set in the actual code are now contained within and provided by the `sdk.token`. These parameters have to be configured beforehand, during the API call that requests the token.

Please refer to the [Configuration section](integration_guide.md#configuration) of our integration guides for a detailed description of all Default UI changes and updates.

Information about which user journey (ID Verification, Identity Verification, Authentication, ...) the SDK is going to provide now also has to be specified during the API call that request the `sdk.token`.

For more details on individual Jumio workflows, please refer to [Workflow Descriptions](https://github.com/Jumio/implementation-guides/blob/master/api-guide/workflow_descriptions.md) in our guides.

#### Custom UI Updates
As of SDK version 4.0.0, Custom UI workflow has been completely restructured.

Please refer to the [Custom UI section](integration_guide.md#custom-ui) of our integration guides for a detailed description of all Custom UI changes and updates.

## 3.9.5
No backward incompatible changes

## 3.9.4
* IProov update: ~~`"com.iproov.sdk:iproov:6.4.1"`~~ is replaced by `"com.iproov.sdk:iproov:6.4.3"`.

## 3.9.3
No backward incompatible changes

## 3.9.2
#### Dependency Changes
* IProov update: ~~`"com.iproov.sdk:iproov:6.3.1"`~~ is replaced by `"com.iproov.sdk:iproov:6.4.1"`. This version improves conversion and offers additional customization options.

#### Customization Updates
* Added additional customization attributes to the IProov theme `Iproov.Customization`:
  * `iproov_headerTextColor`
  * `iproov_headerBackgroundColor`
  * `iproov_footerTextColor`
  * `iproov_footerBackgroundColor`
  * `iproov_livenessScanningTintColor`
  * `iproov_progressBarColor`


## 3.9.1
#### Dependency Changes
* IProov update: ~~`"com.iproov.sdk:iproov:6.3.0"`~~ is replaced by `"com.iproov.sdk:iproov:6.3.1"`. This version fixes cross-dependency problems with okhttp 4.x

#### Customization Updates
* Added attribute `iproov_backgroundColor` to the IProov theme `Iproov.Customization` to allow customization of the IProov background color during scanning.

## 3.9.0
#### Dependency Changes
* IProov update: ~~`"com.iproov.sdk:iproov:6.1.0"`~~ is replaced by "com.iproov.sdk:iproov:6.3.0"

* Room update: ~~`"androidx.room:room-runtime:2.2.5"`~~ is replaced by "androidx.room:room-runtime:2.2.6"

* AndroidX Kotlin Extension update: ~~`"androidx.core:core-ktx:1.3.1"`~~ is replaced by `"androidx.core:core-ktx:1.3.2"`

* JMRTD update: ~~`"org.jmrtd:jmrtd:0.7.19"`~~ is replaced by `"org.jmrtd:jmrtd:0.7.24"`

* Bouncycastle update: ~~`"org.bouncycastle:bcprov-jdk15on:1.65"`~~ is replaced by `"org.bouncycastle:bcprov-jdk15on:1.67"`

* REMOVE LocalBroadcastManager ~~`"androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"`~~

* REPLACE ~~`apply plugin: 'kotlin-android-extensions`~~ with `apply plugin: kotlin-parcelize`. The extensions plugin has been [deprecated by Google](https://goo.gle/kotlin-android-extensions-deprecation). The parcelize functionality has been extracted to a separate plugin.

#### Public API Changes
* `setEnableEMRTD(boolean enable)` has been removed from [NetverifySDK](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html)

* `recreate(Activity rootActivity)` has been added to [NetverifySDK](https://jumio.github.io/mobile-sdk-android/com/jumio/MobileSDK.html#recreate-android.app.Activity-) - this needs to be called in case the hosting activity that was passed in [`create`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#create-android.app.Activity-java.lang.String-java.lang.String-com.jumio.core.enums.JumioDataCenter-) is recreated.

#### Custom UI Changes
* [`NetverifyCustomSDKInterface$onNetverifyFinished`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyFinished-android.os.Bundle-) all parameters were replaced with a Bundle. The keys are defined as constants in the [`NetverifySDK.EXTRA_SCAN_REFERENCE`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html):
 * [`EXTRA_SCAN_REFERENCE`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#EXTRA_SCAN_REFERENCE)
 * [`EXTRA_ACCOUNT_ID`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#EXTRA_ACCOUNT_ID)
 * [`EXTRA_SCAN_DATA`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#EXTRA_SCAN_DATA)


* [`NetverifyCustomSDKInterface$onNetverifyError`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyError-java.lang.String-java.lang.String-boolean-java.lang.String-java.lang.String-) added an optional parameter `accountId`

* New methods for handling host activity lifecycle changes have been added:
  * `recreate(Activity activity, NetverifyCustomSDKInterface netverifyCustomSDKInterface)` has been added to [NetverifyCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html#recreate-android.app.Activity-com.jumio.nv.custom.NetverifyCustomSDKInterface-) - this needs to be called in case the hosting activity is recreated.

  * `recreate(NetverifyCustomScanView scanView, NetverifyCustomConfirmationView confirmationView, NetverifyCustomScanInterface netverifyCustomScanInterface)` has been added to [NetverifyCustomScanPresenter](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#recreate-com.jumio.nv.custom.NetverifyCustomScanView-com.jumio.nv.custom.NetverifyCustomConfirmationView-com.jumio.nv.custom.NetverifyCustomScanInterface-) - this needs to be called in case the hosting activity is recreated.

* The initialization and start of scan presenters has been split. This allows displaying a help view with the help animation prior to starting the scan presenter:
  * `startScanForPart(ScanSide scanSide, NetverifyCustomScanView scanView, NetverifyCustomConfirmationView confirmationView, NetverifyCustomScanInterface scanViewInterface` has been replaced with `initScanForPart(ScanSide scanSide, NetverifyCustomScanView scanView, NetverifyCustomConfirmationView confirmationView, NetverifyCustomScanInterface scanViewInterface)`

  * The following method was added to `NetverifyCustomScanPresenter` to trigger scanning start after the initialization. This method needs to be called on the `NetverifyCustomScanPresenter` after it was initialized with `initScanForPart(..)`.
```
/**
	 * Starts a scan after a scan presenter has been initialized
	 */
	void startScan();
```

  * Make sure to display the `NetverifyCustomScanView` only AFTER calling `startScan()` as done in our [Sample](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/kotlin/netverify/customui/NetverifyCustomScanFragment.kt), to ensure that the scan presenter is fully initialized and the camera callback `onNetverifyCameraAvailable()` will be fired.

* ~~[`NetverifyScanMode.FACE`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyScanMode.html#FACE)~~ is replaced with
  * [`NetverifyScanMode.FACE_MANUAL`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyScanMode.html#FACE_MANUAL) for manual face scanning

  * [`NetverifyScanMode.FACE_IPROOV`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyScanMode.html#FACE_IPROOV) for face scanning with IProov

  * [`NetverifyScanMode.FACE_ZOOM`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyScanMode.html#FACE_ZOOM) for face scanning with Facetec ZoOm

#### Jetifier adaptions
Due to a bug in the Jetifier, the Bouncycastle library needs to be added to the Jetifiers ignorelist in the [`gradle.properties`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/gradle.properties)
```
android.jetifier.blacklist=bcprov-jdk15on
```
Please note that the naming of this will change with the Android Gradle Plugin 4 release and will become `android.jetifier.ignorelist`

## 3.8.0
#### Dependency Changes
* NEW AndroidX Kotlin Extension: `"androidx.core:core-ktx:1.3.1"`

* NEW Kotlin dependency: `"org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0"`

* NEW Kotlin dependency: `"org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0"`

* NEW Kotlin plugin: `"apply plugin: 'kotlinx-serialization"`

* NEW classpath definition: `"classpath "org.jetbrains.kotlin:kotlin-serialization:$kotlin_version"`

* REPLACE Jumio Face: ~~`"com.jumio.android:face"`~~ with either:
  * `"com.jumio.android:iproov:3.8.0@aar"` and `implementation ("com.iproov.sdk:iproov:6.1.0"){ exclude group: 'org.json', module:'json' }`
  __or__
  * `"com.jumio.android:zoom:3.8.0@aar"` and `"com.facetec:zoom-authentication:8.12.1@aar"`


* AndroidX ConstraintLayout update: ~~`"androidx.constraintlayout:constraintlayout:2.0.1"`~~ is replaced by `"androidx.constraintlayout:constraintlayout:2.0.4"`

* AndroidX Appcompat update: ~~`"androidx.appcompat:appcompat:1.1.0"`~~ is replaced by `"androidx.appcompat:appcompat:1.2.0"`

* Google Play Services update: ~~`"com.google.android.gms:play-services-vision:19.0.0"`~~ is replaced by `"com.google.android.gms:play-services-vision:20.1.2"`

* Google Material Library update: ~~`"com.google.android.material:material:1.1.0"`~~ is replaced by `"com.google.android.material:material:1.2.1"`

#### Custom UI Changes
* [`NetverifyCustomSDKController$setDocumentConfiguration`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html#setDocumentConfiguration-com.jumio.nv.custom.NetverifyCountry-com.jumio.nv.data.document.NVDocumentType-com.jumio.nv.data.document.NVDocumentVariant-) does not return a List with all required ScanSides anymore - they are now available as a parameter of [`NetverifyCustomSDKInterface$onNetverifyResourcesLoaded`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyResourcesLoaded-java.util.List-)

* Method `onNetverifyPrepareScanning()` added to `NetverifyCustomScanInterface` - indicates that the SDK is now loading information

#### Proguard Changes
Added the line ` -keep public class com.iproov.sdk.IProov {public *; }` to consumer Proguard rules.

#### Strings and Style Changes
Several additions and changes, mostly in regards to the new liveness flow.

* Button style: ~~`<item name="netverify_confirmationPositiveStyle"> @style/Custom.Netverify.Confirmation.MaterialButton </item>`~~ is replaced by `<item name="materialButtonStyle">@style/Custom.Netverify.Confirmation.MaterialButton</item>`

* Button style: ~~`<item name="netverify_confirmationNegativeStyle"> @style/Custom.Netverify.Confirmation.MaterialButton.Outlined</item>`~~ is replaced by `<item name="materialButtonOutlinedStyle">@style/Custom.Netverify.Confirmation.MaterialButton.Outlined</item>`

* NEW IProov attribute: `<item name="iproov_customization">@style/CustomIproov</item>`

* NEW IProov theme: `<style name="CustomIproov" parent="Iproov.Customization">`

* For changes in `String.xml` file changes please refer to the sample project.

## 3.7.3
#### Custom UI changes
*  The countryList parameter in [NetverifyCustomSDKInterface$onNetverifyCountriesReceived](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyCountriesReceived-java.util.Map-java.lang.String-) has been changed from `HashMap` to `Map`

#### ZoOm Customization Changes
* All the attributes starting with `face_` have been removed
* For a full guide please head over to the [FAQ](integration_faq.md#zoom-customization)

#### Error Code Change
Error code N (Access token for different product / Parts of access token are missing) has been added.
Read more detailed information on this in chapter [Error codes](/docs/integration_guide.md#error-codes)

## 3.7.2
No backward incompatible changes

## 3.7.1
No backward incompatible changes

## 3.7.0

#### Dependency Changes
* Zoom update: ~~"com.facetec:zoom-authentication:8.0.11@aar"~~ is replaced by "com.facetec:zoom-authentication:8.12.1@aar"
* Room updated: ~~"androidx.room:room-runtime:2.2.3"~~ is replaced by "androidx.room:room-runtime:2.2.5"
* JMRTD updated: ~~"org.jmrtd:jmrtd:0.7.18"~~ is replaced by "org.jmrtd:jmrtd:0.7.19"
* Bouncycastle updated: ~~"org.bouncycastle:bcprov-jdk15on:1.64"~~ is replaced by "org.bouncycastle:bcprov-jdk15on:1.65"

#### API Changes
* `getEMRTDStatus()` has been removed from [NetverifyDocumentData](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDocumentData.html)
* New attributes for customization of the NFC help animation have been added to our `styles.xml`:
  * `netverify_nfc_passport_cover` - Outside passport cover color
  * `netverify_nfc_passport_page_dark` - Color of the last page inside the passport after opening
  * `netverify_nfc_passport_page_light` - Color of the flipped pages during opening
  * `netverify_nfc_passport_foreground` - Foreground color for the passport text and the phone screen
  * `netverify_nfc_phone_background` - Frame color for the phone that is positioned on the passport

* Attribute `netverify_nfc_dialog_theme` has been removed and is not required any more
* The position of the Jumio branding logo and privacy link changed to from bottom-right to center-top for all portrait scan views. In Custom UI, the top margin for this element can be adjusted using the following method [`setBrandingLogoTopMargin(int topMargin)`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanView.html#setBrandingLogoTopMargin-int-)

#### Customization Updates
* Android Zoom screens update. Details can be found in [Custom theme issues](known_issues.md#custom-theme-issues)

#### Localizable Strings
Several additions and changes in regards to the Android Zoom screens update.

__Added strings:__
* [`netverify_nfc_description_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L58-L65) strings
* [`zoom_action_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L107-L108) strings
* [`zoom_instructions_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L127-L128) strings
* [`zoom_result_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L129-L130) strings
* [`zoom_retry_xxx`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml#L131-L136) strings

__Removed strings:__
* `netverify_nfc_bac_dialog_xxx` strings
* `netverify_nfc_description_xxx` strings

## 3.6.2
The Proguard keep rule `-keep class com.jumio.** { *; }` has to be added to your Proguard rules, if it wasn't added yet.
Details can be found in chapter [Proguard](../README.md#proguard)

## 3.6.1
No backward incompatible changes

## 3.6.0
#### Dependency Changes
AndroidX Material design library has been *updated to version 1.1.0*
* ~~"com.google.android.material:material:1.0.0"~~ is replaced by "com.google.android.material:material:1.1.0"

Local broadcast manager dependency has been added mandatory due to the design library update where it was separated by Google
* implementation "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"

JMRTD is now added as a gradle dependency - if you use NFC scanning please make sure to add the new dependencies:
* implementation "org.jmrtd:jmrtd:0.7.18"
* implementation "org.ejbca.cvc:cert-cvc:1.4.6"

## 3.5.0
#### Error Code Change
* Error code D (Wrong API credentials used, retry impossible) has been removed.

#### Proguard Change
* Consumer proguard rules have been added. All Jumio SDK proguard rules will now be applied automatically to the application when the Jumio Core library is included.

## 3.4.1
#### API Changes in NetverifyCustomScanInterface
* A new callback [onNetverifyUserConsentRequired](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKInterface.html#onNetverifyUserConsentRequired-java.lang.String-) was added to [NetverifyCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html)
#### API Changes in NetverifyCustomSDKController
* A new method [onUserConsented](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html#onUserConsented--) was added to [NetverifyCustomSDKController](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomSDKController.html)

## 3.4.0
#### Customization Updates
* The SDK now uses Material buttons instead of the old snackbar button styles on the confirmation view.
* Please check out the confirmation screens and the XML output in the [Surface Tool](https://jumio.github.io/surface-android/) for all adapted content.
Changes:
* New style attribute **confirmationIcon** for customizing the info and warning icon on the confirmation screen.
* New confirmation button style **Netverify.Confirmation.MaterialButton** for the positive button.
* New confirmation button style **Netverify.Confirmation.MaterialButton.Outlined** for the negative button.

#### Dependency Changes
* Constraint layout dependency **androidx.constraintlayout:constraintlayout:1.1.3** or higher is now mandatory
* The following plugins are now required in the build.gradle of your application:
```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
```

* Dependency name and version change - com.facetec:zoom-authentication-hybrid:7.0.14 is replaced by **com.facetec:zoom-authentication:8.0.11@aar**
*  ~~androidx.appcompat:appcompat:1.0.2~~ is replaced by androidx.appcompat:appcompat:1.1.0
*  ~~androidx.room:room-runtime:2.0.0~~ is replaced by androidx.room:room-runtime:2.2.1

#### Custom UI Callbacks
*  ~~onNetverifyDisplayFlipDocumentHint~~ has been removed
*  A new parameter [NetverifyConfirmationType](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyConfirmationType.html) was added to [NetverifyCustomScanInterface$onNetverifyPresentConfirmationView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html#onNetverifyPresentConfirmationView-NetverifyConfirmationType-)

#### Proguard Changes
`-dontwarn com.facetec.zoom.sdk.**` needs to be added

## 3.3.2
No backward incompatible changes

## 3.3.1
No backward incompatible changes

## 3.3.0
#### Deallocation Callback
Added a new method [checkDeallocation](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#checkDeallocation-com.jumio.nv.NetverifyDeallocationCallback-) in the [NetverifySDK](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html) to check if the SDK resources have already been deallocated. The method requires a  [NetverifyDeallocationCallback](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDeallocationCallback.html) instance as a parameter and calls `onNetverifyDeallocated` once the SDK is deallocated. The checkDeallocation method should only be called once the SDK has returned a result and another SDK instance is required.

#### Dependency Change
*  ~~com.facetec:zoom-authentication-hybrid:7.0.12~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.14

#### Date Changes
All dates are now UTC based. This affects the dates in [NetverifyDocumentData](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifyDocumentData.html) and [NetverifyCustomNfcAccess](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcAccess.html)

#### New SDK Localizations Added
SDK Translations for the languages Italian and Portuguese have been added.

## 3.2.1
#### Fixed a Face Scanning Problem in Which a Black Screen Was Shown to the User

## 3.2.0
#### Dependency Change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2
*  ~~com.facetec:zoom-authentication-hybrid:7.0.9~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.12

#### API Changes in NetverifyCustomScanInterface
A new parameter [NetverifyCancelReason](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCancelReason.html) was added to [NetverifyCustomScanInterface$onNetverifyScanForPartCanceled](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html#onNetverifyScanForPartCanceled-com.jumio.core.data.document.ScanSide-NetverifyCancelReason-)

#### Watchlist Screening
Two new methods for handling watchlist screening have been added. Please refer to the [Netverify guide](/docs/integration_bam-checkout.md#watchlist-screening)
```
netverifySDK.setWatchlistScreening(NVWatchlistScreening.ENABLED);
netverifySDK.setWatchlistSearchProfile("YOURPROFILENAME");
```

#### Additional Functions in NetverifyCustomScanInterface
For the new NFC scanning functionality in custom ui, there has been some additions in [NetverifyCustomScanInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanInterface.html):
`getNetverifyCustomNfcInterface()` should return an instance of [NetverifyCustomNfcInterface](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcInterface.html) or Null
`onNetverifyStartNfcExtraction(NetverifyCustomNfcPresenter)` is called when the NFC scanning can be started. The NFC scanning is controlled with the provided instance of [NetverifyCustomNfcPresenter](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/nfc/custom/NetverifyCustomNfcPresenter.html)

#### Customization
The following customization attribute have been added: `netverify_scanOverlayFill`

#### New Fastfill Offline Token Required
Due to a license model update in our Barcode implementation, it's necessary to regenerate the offline token when updating to version 3.2.0  

#### Close Button cCustomization in Custom UI
The position and image of the close button for face scanning can now be customized. Please have a look at the [NetverifyCustomScanView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanView.html)

#### Custom UI Help Animation Support
[NetverifyCustomScanPresenter$getHelpAnimation](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomScanPresenter.html#getHelpAnimation-com.jumio.nv.custom.NetverifyCustomAnimationView-) has been added to get the specific help animation in case the scan part is canceled. An instance of  [NetverifyCustomAnimationView](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/custom/NetverifyCustomAnimationView.html) needs to be passed where the animation is rendered in.

## 3.1.0
#### Dependency Changes
*  ~~com.madgag.spongycastle:prov:1.58.0.0~~ is replaced by org.bouncycastle:bcprov-jdk15on:1.61
*  Proguard rules containing *org.spongycastle* have been replaced with *org.bouncycastle*
*  ~~com.facetec:zoom-authentication-hybrid:7.0.5~~ is replaced by com.facetec:zoom-authentication-hybrid:7.0.9

## 3.0.0
#### Renamed Dependency NV-Face to Face
The dependency `com.jumio.android:nv-face` was renamed to `com.jumio.android:face`, reflecting the internal restructuring of the dependencies that was necessary for adding the new product Authentication

#### Renamed Public API Methods and Parameters
The following methods and the related parameters have been renamed to ensure consistency across all platforms
* `setRequireFaceMatch(..)` -> `setEnableIdentityVerification(..)`
* `setRequireVerification(..)` -> `setEnableVerification(..)`
* `setMerchantReportingCriteria(..)` -> `setReportingCriteria(..)`
* `setMerchantIdScanReference(..)` -> `setCustomerInternalReference(..)`
* `setCustomerId(..)` -> `setUserReference(..)`

#### Additional Property in NetverifyCustomScanView
`setMode(..)` must be called before the view is used. Possible values: NetverifyCustomScanView.MODE_ID or NetverifyCustomScanView.MODE_FACE


## 2.15.0
#### Added Room
Dependencies that have been added to the SDK:
+ androidx.room:room-runtime:2.0.0

#### Added 3D Liveness
###### Dependency Changes
* com.jumio.android:nv-face:2.15.0@aar
* com.facetec:zoom-authentication-hybrid:7.0.2@aar
* ~~com.jumio.android:nv-liveness:2.14.0@aar~~ The old liveness module is not supported anymore

The Facetec Maven repository also needs to be added: maven { url 'http://maven.facetec.com' }

###### Localization
Please have a look at the [strings-jumio-sdk.xml](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/res/values/strings-jumio-sdk.xml) for all the new added strings prefixed with `zoom_`.
The following strings have been removed: `netverify_scanview_liveness_follow_hint`, `netverify_scanview_liveness_move_closer`, `netverify_scanview_liveness_move_back`, `netverify_scanview_liveness_description`, `netverify_helpview_full_description_liveness_glasses`, `netverify_helpview_full_description_liveness_cap` and `netverify_helpview_full_description_liveness_light`.

###### Customization
The following customization attributes have been added: `netverify_scanOverlayFaceBackground`, `netverify_scanOverlayFaceFeedbackText`, `netverify_scanOverlayFaceFeedbackBackground`, `netverify_scanOverlayFaceProgress`, `netverify_scanOverlayFaceOval`
The following customization attributes have been removed: `netverify_scanOverlayLivenessValid`, `netverify_scanOverlayLivenessInvalid`, `netverify_scanOverlayLivenessBackground`, `netverify_scanOverlayLivenessText`

###### Proguard
The following lines need to be added in your `proguard-rules.pro` file for 3D Liveness:
```
-keep class com.facetec.zoom.** { *; }
-dontwarn javax.annotation.Nullable
```

## 2.14.0
#### Migrate to AndroidX
The support library was migrated to [`AndroidX`](https://developer.android.com/jetpack/androidx/). As the developer page outlines, this is a mandatory step since all new Support Library development and maintenance will occur in the AndroidX library. This [`migration guide`](https://developer.android.com/jetpack/androidx/migrate) shows you how to migrate your application to AndroidX.

Check out the changed dependencies in the  [`dependencies section`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_guide.md#dependencies) or in the [`build.gradle`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of the sample application.
The mapping for all support libraries is listed in section "Artifact mappings" [here](https://developer.android.com/jetpack/androidx/migrate)

Dependencies that changed in the SDK:
+ com.android.support:appcompat-v7:27.1.1 -> androidx.appcompat:appcompat:1.0.0
+ com.android.support:cardview-v7:27.1.1 -> androidx.cardview:cardview:1.0.0
+ com.android.support:design:27.1.1 -> com.google.android.material:material:1.0.0
- com.android.support:support-v4:27.1.1 -> androidx.legacy:legacy-support-v4:1.0.0 (was merged by AndroidX and can be therefore be fully removed)

#### Default Settings
The default values for [`requireVerification`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#setRequireVerification-boolean-) and [`requireFaceMatch`](https://jumio.github.io/mobile-sdk-android/com/jumio/nv/NetverifySDK.html#setRequireFaceMatch-boolean-) were changed to `true`. Please make sure that they are explicitly set to false in case a scan in Fastfill mode should be performed.

## 2.13.0
#### API Change in NetverifyDocumentData
The function `getMiddleName()` has been removed. If a middle name is available, it will be contained with the first name.

#### Removed Deprecated ABIs mips, mips64 and armeabi
These ABIs were deprecated in recent NDK toolsets as mentioned here - https://developer.android.com/ndk/guides/abis and are not used any more.

## 2.12.1
#### Fixed a Problem in Which the User Could Get Stuck in the Selfie Capturing Process

## 2.12.0
#### Fallback for Google Vision Not Operational Added
A fallback to manual image picker will now be used if Google Vision is not operational due to problems on the device. This guarantees the face workflow to be finished despite problems with the availablility of the Google Play services. Details are also described in [sub-chapter operationality](integration_guide.md#operationality).
The method `netverifySDK.isMobileVisionOperational` remains in the SDK but doesn't need to be checked now necessarily.

#### New SDK Localizations Added
In addition to English, the translations for the languages Chinese (Simplified), Dutch, French, German and Spanish have been added.

#### Remove Unused Strings for Localization
Along with the additional languages, we removed some Strings that were unused in the SDK. The following keys have been removed: `netverify_confirmation_snackbar_help_default`, `netverify_accessibility_select_your_country`, `netverify_accessibility_action_double_click`, `netverify_accessibility_select_your_country`, `netverify_scan_options_country_title`, `netverify_overlay_liveness_advice` and `netverify_scan_options_preselected_hint`.

#### Additional Information Method Removed
SDK method `netverifySDK.setAdditionalInformation` has been removed.

#### New Callback in NetverifyCustomScanInterface
`onNetverifyDisplayBlurHint()` was added for custom scan view.

## 2.11.0
#### New Error Scheme
The schema for `errorCode` changed and it's type is now String instead of Integer.
Read more detailed information on this in chapter [Error codes](/docs/integration_guide.md#error-codes)

## 2.10.1
No backward incompatible changes.

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)

## 2.9.0

#### Changes in SDK Code
* New cardview dependency was added `com.android.support:cardview-v7:26.1.0` for the screen redesign. This dependency is mandatory for Netverify
* Multidex is now mandatory, follow the steps Android Developers guide https://developer.android.com/studio/build/multidex.html#mdex-gradle to enable it if necessary in your app.
* Additional Proguard rules for the updated Barcode Scanner have to be added:
```
-keep class com.microblink.** { *; }
-keep class com.microblink.**$* { *; }
-dontwarn com.microblink.**
```
* SDK method for checking the Google Mobile Vision API operationality was added (see method documentation in [NetverifyFragment](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/src/main/java/com/jumio/sample/NetverifyFragment.java) in the Sample app))
```
GoogleVisionStatus NetverifySDK.isMobileVisionOperational(Activity activity, int requestCode);
```
The usage is explained in the Netverify guide [sub-chapter operationality](integration_guide.md#operationality)

#### Changes in Localizable Strings
Multiple additions and changes in regards to the new selection screen.

#### Changes in Customization
Additions and changes in regards to the new selection screen (see XML output in [Surface Tool](https://jumio.github.io/surface-android/)).
Attributes added for replacing the previous selection screen: `netverify_scanOptionsItemHeaderBackground`, `netverify_scanOptionsItemForeground` and `netverify_scanOptionsItemBackground`.

## 2.8.0
* Dependency `com.jumio.android:nv-liveness:2.8.0@aar` is mandatory now.

## 2.7.0
* New Dependency `com.jumio.android:nv-liveness:2.7.0@aar` was added for face-liveness functionality.
* Dependency `com.google.android.gms:play-services-vision` is now mandatory required because of added functionality.
* Change SDK method `setEnableEpassport(boolean)` to `setEnableEMRTD(boolean)` because of to the support for NFC ID documents.
* If the dependencies `com.jumio.android:nv-liveness` and `com.jumio.android:nv-barcode-vision` are both used in the application, the following lines have to be added to the application tag in the AndroidManifest.xml to avoid merge issues.
```
<meta-data
			android:name="com.google.android.gms.vision.DEPENDENCIES"
			android:value="barcode, face"
			tools:replace="android:value"/>
```

* Additional Proguard rules for the Samsung Camera SDK have to be added:
```
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**
```

## 2.6.1
No backward incompatible changes.

## 2.6.0

#### Changes in SDK API
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Add NetverifySDK method `isRooted(Context)` for device root-check before starting the SDK

#### Changes in Localizable Strings
Multiple additions and changes in regards to the new guidance / help screen.

#### Changes in Customization
Additions and changes in regards to the new guidance / help screen.

## 2.5.0
No backward incompatible changes.

## 2.4.0

#### Remove okHttp
The build.gradle was adapted to support standard UrlConnection for replacing okHttp

#### Changes in Customization
Override the theme that is used for Netverify in the manifest by calling `netverifySDK.setCustomTheme(CUSTOMTHEMEID)`. Use the resource id of a customized theme that uses `Theme.Netverify` as parent.
Additions and changes for customization options for the launch of the surface tool.

#### Provide Possibility to Avoid Loading Spinner After SDK Start
Use the following method to initialize the SDK before displaying it
```
netverifySDK.initiate(new NetverifyInitiateCallback() {
 @Override
 public void onNetverifyInitiateSuccess() {
 }
 @Override
 public void onNetverifyInitiateError(int errorCode, int errorDetail, String errorMessage, boolean retryPossible) {
 }
});
 ```
#### Removed Name Match Feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
#### Changes in Customization
Additions for the customization options to support the configuration of all scan overlays.


## Copyright

&copy; Jumio Corp. 395 Page Mill Road, Palo Alto, CA 94306
