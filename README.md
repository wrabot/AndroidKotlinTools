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
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-persistent:0.16'
        implementation 'com.github.wrabot.AndroidKotlinTools:tools-compose:0.16'
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

## tools-compose

Needs only basic compose dependencies

Ease composable navigation with just a state and a view model

The following sample will explain how to create load a list and choice an with a back and a correct slide animation between each screens.

A state defines all UI states with a comparable sealed class (comparable wil be used by CrossSlide)

```kotlin
sealed class State(private val ordinal: Int) : Comparable<State> {
    override fun compareTo(other: State) = ordinal - other.ordinal

    object Loading : State(0)
    data class List(val items: List<Item>) : State(1)
    data class Details(val item: Item) : State(2)
}
```

A view model holds the back stack (and loading functions)

```kotlin
class MyViewModel : ViewModel() {
    val backStack = BackStack<State>(State.Loading)

    suspend fun loadItems(): List<Item> {
        //TODO
    }
}
```

Thanks to "BackStack" and "CrossSlide" defined in this library, this function will shows the correct screen with a slide animation

```kotlin
@Composable
fun MyFlow(viewModel: MyViewModel = viewModel()) =
    Column(Modifier.fillMaxSize()) {
        BackHandler(viewModel.backStack.hasBack) { viewModel.backStack.back() }
        CrossSlide(viewModel.backStack.current) { state ->
            when (state) {
                State.Loading -> {
                    LoadingScreen()
                    LaunchedEffect(Unit) {
                        // when loaded, display list without memorizing previous state
                        viewModel.backStack.current = State.List(viewModel.loadItems())
                    }
                }
                is State.List -> ListScreen(state.items) { choice ->
                    // when clicking on item, display item with memorizing previous state
                    viewModel.backStack.next(State.Item(choice))
                }
                is State.Details -> List(state.item)
            }
        }
    }
```

**BackStack**: it holds a state and simplifies back management for a composable.

**CrossSlide**: it animates content depending on a "Comparable" state.

When going to "upper" state, it slides from right to left

When going to "lower" state, it slides from left to right

When staying on "same" state, it fades in/out
