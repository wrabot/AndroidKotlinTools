# Android Kotlin Tools

**UNDER CONSTRUCTION => DO NOT USE NOW**

Set of tools that I use in my Android projects.

They are split in modules to avoid including all dependencies and permissions

## tools-base
Needs no dependencies or permissions

**SharedPreferencesManager**: map kotlin properties to SharedPreferences easily

**SpannableStringBuilder extensions**: create spannable easily

**Throwable extension**: find a cause with a predicate

## tools-reflect
Needs only reflection

**checkNullable**: check by reflection that kotlin mandatory members are really not null.
This is useful when kotlin objects are created with java reflection (like with Gson).

## tools-rx-base
Needs only RX

**ForegroundManager**: create an object which detects application background/foreground

## tools-rx-databinding
Needs RX and data binding

**RxSimpleAdapter**: use easily recycler views with RX adapter

## tools-rx-fingerprint
Needs RX and USE_FINGERPRINT permission

**FingerPrintHelper**: cipher and decipher data with fingerprint through RX

## tools-rx-retrofit
Needs RX and retrofit

**RxCallAdapterFactory**: create a Retrofit call adapter factory for RX with the ability to add retry

## installation

Add the following repository

    repositories {
        maven { url 'https://jitpack.io' }
    }
    
Add the needed dependencies (just one or several)

    dependencies {
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-base:0.1'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-reflect:0.1'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-base:0.1'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-databinding:0.1'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-fingerprint:0.1'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-retrofit:0.1'
    }
