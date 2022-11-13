# Android Kotlin Tools

Set of tools that I use in my Android projects.

NB1: Old versions used RX but since coroutines, live data, flows are well defined, I migrate and simplify my tools.

NB2: Since Jetpack Compose, list with data binding and view binding have been removed from this library.

They are split in modules to avoid including all dependencies and permissions

## installation

Add the following repository

    repositories {
        maven { url 'https://jitpack.io' }
    }

Add the needed dependencies (just one or several)

    dependencies {
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-persistent:0.14'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-compose:0.14'
    }

## tools-persistent

Needs no dependencies or permissions

**SharedPreferencesManager**: it maps kotlin properties to SharedPreferences easily.

With extended this class, kotlin properties can maps the shared preferences content.

```kotlin
class MyPreferencesManager(context: Context) : SharedPreferencesManager(context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)) {
    var myString by StringDelegate()
    var myInt by IntDelegate()
    var myBool by BooleanDelegate()
}
```

In this example myString, myInt, myBool are retrieved and stored automatically in shared preferences with this names.

## tools-persistent

Needs only basic compose dependencies

**CrossSlide**: it animates content depending on a "Comparable" state.

When going to "upper" state, it slides from right to left

When going to "lower" state, it slides from left to right

When staying on "same" state, it fades in/out

