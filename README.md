# Android Kotlin Tools

Set of tools that I use in my Android projects.

NB1: Old version used RX but since coroutines, live data, flows are well defined, I migrate and simplify my tools.

NB2: data binding will be replaced by view binding for list in future releases.

They are split in modules to avoid including all dependencies and permissions

## installation

Add the following repository

    repositories {
        maven { url 'https://jitpack.io' }
    }
    
Add the needed dependencies (just one or several)

    dependencies {
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-persistent:0.11'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-viewbinding:0.11'

        // deprecated
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-databinding:0.11'
    }
    
## tools-persistent
Needs no dependencies or permissions

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

## tools-viewbinding
Deprecated: I prefer compose

Needs view binding

**SimpleListAdapter**: use easily recycler views with adapter and LiveData for one item type

This adapter allows automatic updates through LiveData and easy item provisioning with view binding

The item POJO :
```kotlin
data class Item(val text : String) {
    fun bind(binding: ItemBinding) {
        binding.text.text = text
    }
}
```

The item layout described :
```xml
<android.support.v7.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:layout_margin="5dp"
    android:foreground="?attr/selectableItemBackground"
    app:cardCornerRadius="4dp">
        <TextView
            android:id="@+id/text"
            android:layout_width="match_parent"
            android:layout_height="50dp"/>
</android.support.v7.widget.CardView>
```

The following method are generated by view binding:
- ItemBinding::inflate (generated for every binding class) is used to inflates item

The livedata which will provided data in view model
```kotlin
val data = MutableLiveData<List<Item>>()
```

Once, define the adapter
```kotlin
recyclerView.adapter = SimpleListAdapter(ItemBinding::inflate, Item::bind).apply {
    viewModel.data.observe(this) {submitList(it)}
    onClick = { item, position, view -> println("$item at $position in $view is clicked") }
}
```

**MultiListAdapter**: use easily recycler views with adapter and LiveData for several item type

This adapter allows automatic updates through LiveData and easy item provisioning with view binding

The first layout described named item_text.xml :
```xml
<TextView
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

The first binder :
```kotlin
    fun String.bind(binding: ItemTextBinding) {
        binding.text.text = this
    }
```

The second layout described named item_image.xml :
```xml
<ImageView
    android:id="@+id/image"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

The second binder :
```kotlin
    fun Drawable.bind(binding: ItemImageBinding) {
        binding.image.setImageDrawable(this)
    }
```

NB: String and Drawable replaced by any other class

The item declarations with content, inflate method, and bind method :
```kotlin
data class Item1(content : String) : MultiListAdapter.Item<String>(content, ItemTextBinding::inflate, String::bind)
data class Item2(content : Drawable) : MultiListAdapter.Item<Drawable>(content, ItemImageBinding::inflate, Drawable::bind)
```

Once, define the adapter
```kotlin
recyclerVew.adapter = MultiListAdapter()
```

Update adapter a list of Item1 and/or Item2
```kotlin
adapter.submitList(listOf(item1a, item2a, item1b))
```
