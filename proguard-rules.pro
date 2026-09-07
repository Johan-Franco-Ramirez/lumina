# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\harol\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature, Exceptions

# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Room
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**
