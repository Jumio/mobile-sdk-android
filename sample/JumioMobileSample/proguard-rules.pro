# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#Jumio
-keep class com.jumio.** { *; }
-keep class jumio.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn org.jetbrains.annotations.ApiStatus$Internal
-dontwarn org.jetbrains.annotations.ApiStatus$ScheduledForRemoval

#Tensorflow
-keep class org.tensorflow.** { *; }
-keep class org.tensorflow.**$* { *; }
-dontwarn org.tensorflow.**

#IProov
-keep public class com.iproov.sdk.IProov {public *; }
-keep class com.iproov.** { *; }
-keep class com.iproov.**$* { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.google.protobuf.**$* { *; }
-dontwarn com.google.protobuf.**
-dontwarn com.tinder.**
-dontwarn okhttp3.**
-dontwarn okio.**

#Datadog
-keep class com.datadog.** { *; }
-keep class com.datadog.**$* { *; }

#JMRTD
-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.ejbca.** {*;}
-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn org.ejbca.**
-dontwarn org.bouncycastle.**
-dontwarn module-info