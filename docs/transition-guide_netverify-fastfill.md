![Fastfill & Netverify](images/netverify.png)

# Transition guide for Netverify & Fastfill SDK

## 2.7.0
* New Dependency `com.jumio.android:nv-liveness:2.7.0@aar` was added for face-liveness functionality.
* Dependency `com.google.android.gms:play-services-vision` is now mandatory required because of added functionality.
* Change SDK method `setEnableEpassport(boolean)` to `setEnableEMRTD(boolean)` beacause of to the support for NFC ID documents.

## 2.6.1
No backward incompatible changes.

## 2.6.0

#### Changes in SDK Api
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Add NetverifySDK method `isRooted(Context)` for device root-check before starting the SDK

#### Changes in localizable strings
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

#### Provide possibility to avoid loading spinner after SDK start
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
#### Removed name match feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
#### Changes in Customization
Additions for the customization options to support the configuration of all scan overlays.


## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
