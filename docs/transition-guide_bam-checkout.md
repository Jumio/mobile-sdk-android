![BAM Checkout](images/bam_checkout.png)

# Transition guide for BAM Checkout SDK

## 2.9.0
No backward incompatible changes.

## 2.8.0
No backward incompatible changes.

## 2.7.0
* Removed SDK method `setAdyenPublicKey(String)` along removing all adyen functionality.
* Additional Proguard rules for the Samsung Camera SDK have to be added:
```
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**
```

## 2.6.1
No backward incompatible changes.

## 2.6.0
No backward incompatible changes.

## 2.5.0

#### BAM Checkout SDK offline with an offline token
Added the option to create an SDK instance for offline scanning with `BamSDK.create(rootActivity, YOUROFFLINETOKEN)`

## 2.4.0
#### Removed name match feature
Name matching by comparing a provided name with the extracted name from a document was removed. The method `setName("FIRSTNAME LASTNAME")` in the NetverifySDK was removed.

## 2.3.0
No backward incompatible changes.

## 2.2.0
#### Changes in Customization
Multiple additions and changes in regards to a new user interface design to BAM Checkout Mobile


## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
