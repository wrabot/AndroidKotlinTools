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
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-base:0.9'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-databinding:0.9'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-base:0.9'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-databinding:0.9'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-rx-fingerprint:0.9'
    }
    
## tools-base
Needs no dependencies or permissions

**enumValueOrDefault** : parse enum with default value

**ForegroundManager**
**Deprecated : use ProcessLifecycleOwner**

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

**SimpleListAdapter**: use easily recycler views with adapter and LiveData for one item type

This adapter allows automatic updates through LiveData and easy item provisioning with data binding

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

The livedata which will provided data in view model
```kotlin
val data = MutableLiveData<List<Item>>()
```

Once, define the adapter
```kotlin
recycler_view.adapter = SimpleListAdapter(ItemBinding::inflate, ItemBinding::setItem).apply {
    viewModel.data.observe({livecycle}) {submitList(it)}
    onClick = { item, position, view -> println("$item at $position in $view is clicked") }
}
```

**MultiListAdapter**: use easily recycler views with adapter and LiveData for several item type

This adapter allows automatic updates through LiveData and easy item provisioning with data binding

The item layout described with data binding :

- layout item1
```xml
<layout>

    <data>
        <variable
            name="item1"
            type="com.example.Item1"/>
    </data>
    
    ...

</layout>
```

- layout item2
```xml
<layout>

    <data>
        <variable
            name="item2"
            type="com.example.Item2"/>
    </data>
    
    ...

</layout>
```

The item declarations with content, layout Id, and variable Id for data binding :
```kotlin
data class Item1(content : Item1) : MultiListAdapter.Item<Item1>(content, R.layout.item1, BR.item1)
data class Item2(content : Item2) : MultiListAdapter.Item<Item2>(content, R.layout.item2, BR.item2)
```

Once, define the adapter
```kotlin
recycler_view.adapter = MultiListAdapter()
```

Update adapter a list of Item1 and/or Item2
```kotlin
adapter.submitList(listOf(item1a, item2a, item1b))
```

## tools-rx-base
Needs only RX (depends on tools-base)

**DownloadManager.progress**: RX flowable which observes several downloads (see KDoc).

**RxViewModel,RxAndroidViewModel** : ViewModel with a disposable automatically cleared

**ForegroundManager**
**Deprecated : use ProcessLifecycleOwner**

**View.animate**: animate view with rx
```kotlin
// define generic animation with View.animate which returns a Completable
fun View.fadeIn(duration: Long) = animate {
    setDuration(duration)
    alpha(1f)
}

// use rx to compose animations
val animation = view1.fadeIn(1000).andThen(view2.fadeIn(500))

// start animation
animation.subscribe()
```

## tools-rx-databinding
**Deprecated : use SimpleListAdapter/MultiListAdapter**

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
recycler_view.adapter = RxSimpleAdapter(subject, emptyList(), ItemBinding::inflate, ItemBinding::setItem)
```

RxSimpleAdapter provides also PublishSubject called "clicks" or "clickEvents" which allow to handle clicks on items
```kotlin
adapter.clicks.bindToLifecycle(this@MyActivity).subscribe {
    println("${it.title} is clicked")
}
```

## tools-rx-fingerprint
**Deprecated : use FingerprintPrompt**
