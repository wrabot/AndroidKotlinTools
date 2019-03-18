# Android Kotlin Tools

Set of tools that I use in my Android projects.

They are split in modules to avoid including all dependencies and permissions

## installation

Add the following repository

    repositories {
        maven { url 'https://jitpack.io' }
    }
    
Add the needed dependencies (just one or several)

    dependencies {
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-base:0.6'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-databinding:0.6'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-base:0.6'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-databinding:0.6'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-fingerprint:0.6'
    }
    
## tools-base
Needs no dependencies or permissions

**enumValueOrDefault** : parse enum with default value

**ForegroundManager**: create an object which detects application background/foreground

Add the following line in your application class
```kotlin
open class MyApp : Application() {
    override fun onCreate() {
        ...
        registerActivityLifecycleCallbacks(ForegroundManager)
    }
}
```

Anywhere in the application, an background/foreground event can be receive
```kotlin
ForegroundManager.foreground.observeForever {
    if (foreground) {
    } else { 
    }
}
```

**SharedPreferencesManager**: map kotlin properties to SharedPreferences easily.

With extended this class, kotlin properties can maps the shared preferences content.
```kotlin
class MyPreferencesManager(context: Context) : SharedPreferencesManager(context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)) {
    var myString by StringDelegate()
    var myInt by IntDelegate()
    var myBool by BooleanDelegate()
}
```

In this example myString, myInt, myBool are retrieved and stored automatically in shared preferences with this names.

**Throwable extension**: find a cause with a predicate (see KDoc).

**Views utilities**: hide/show views easily (see KDoc).

## tools-databinding
Needs data binding

**SimpleListAdapter**: use easily recycler views with adapter and LiveData

This adapter allows automatic updates updates through LiveData and easy item provisioning with data binding

The item POJO :
```kotlin
data class Item(val title : String, val id : Long)
```

The item layout described with data binding :
```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@layout/item">

    <data>
        <variable
            name="item"
            type="com.example.Item"/>
    </data>

    <android.support.v7.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_margin="5dp"
        android:foreground="?attr/selectableItemBackground"
        app:cardCornerRadius="4dp">
            <TextView
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:text="@{item.title}"/>
    </android.support.v7.widget.CardView>
</layout>
```
The main layout which contains the recycler view (in the example) :
```xml
<android.support.v7.widget.RecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layoutManager="LinearLayoutManager"
    tools:listitem="@layout/item"/>
```

The following methods are generated by databinding:
- ItemBinding::inflate (generated for every binding class) is used to inflates item
- ItemBinding::setItem (generated by the variable tag) is used to bind item data

The subject (or the Observable) which will provided data
```kotlin
val data = MutableLiveData<List<Item>>()
```

Once, define the adapter
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    recycler_view.adapter = SimpleListAdapter(ItemBinding::inflate, ItemBinding::setItem).apply {
        data.observe({livecycle}) {submitList(it)}
    }
}
```

SimpleListAdapter provides also onClick variable which allow to handle clicks on items
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    recycler_view.adapter = RxSimpleAdapter(subject, emptyList(), ItemBinding::inflate, ItemBinding::setItem, Item::id).apply {
        onClick = { item, position, view -> println("$item at $position in $view is clicked") }
    }
}
```

## tools-rx-base
Needs only RX

**DownloadManager.progress**: RX flowable which observes several downloads (see KDoc).

**RxViewModel,RxAndroidViewModel** : ViewModel with a disposable automatically cleared

**ForegroundManager**: Deprecated see tools-base

## tools-rx-databinding
**Will be replaced by SimpleListAdapter**
Needs RX and data binding

**RxSimpleAdapter**: use easily recycler views with RX adapter

This adapter allows automatic updates updates through RX and easy item provisioning with data binding 

See SimpleListAdapter for Item definition (POJO and layout)

The subject (or the Observable) which will provided data
```kotlin
val subject = BehaviorSubject<List<Item>>.create()
```

Once, only one line to define the adapter
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    recycler_view.adapter = RxSimpleAdapter(subject, emptyList(), ItemBinding::inflate, ItemBinding::setItem)
}
```

RxSimpleAdapter provides also PublishSubject called "clicks" or "clickEvents" which allow to handle clicks on items
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    recycler_view.adapter = RxSimpleAdapter(subject, emptyList(), ItemBinding::inflate, ItemBinding::setItem, Item::id).apply {
        clicks.bindToLifecycle(this@MyActivity).subscribe {
            println("${it.title} is clicked")
        }
    }
}
```

## tools-rx-fingerprint
Needs RX and USE_FINGERPRINT permission

**FingerPrintHelper**: cipher and decipher data with fingerprint through RX


```kotlin
val helper = FingerprintHelper(context)
```

```kotlin
helper.cipher(plain).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(this).subscribe {
    when (it) {
        is FingerprintHelper.Event.Help -> show(it.help)
        FingerprintHelper.Event.Failure -> handleFailure()
        is FingerprintHelper.Event.Error -> show(it.error)
        is FingerprintHelper.Event.Success -> storeCipheredSecret(it.result)
    }
}
```

```kotlin
helper.decipher(secret).observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(this).subscribe {
    when (it) {
        is FingerprintHelper.Event.Help -> show(it.help)
        FingerprintHelper.Event.Failure -> handleFailure()
        is FingerprintHelper.Event.Error -> show(it.error)
        is FingerprintHelper.Event.Success -> usePlainSecret(it.result)
    }
}
```
