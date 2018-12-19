# Implementing Shards 

To implement a shard you need to subclass `Shard`. You only have a single callback `onCreate()` 
where you should do any setup logic. After you implement your shard, you'll want to show it somehow,
see [Hosting Shards](/docs/hosting-shards.md) for your options.

```kotlin
class MyShard: Shard() {
    override fun onCreate() {
        // Setup code
    }
}
```

## Views

You can set the contents of the shard with `setContentView(layout)` or `setContentView(view)`. You
can find views with `findVieById()` or `requireViewById()`. You can access the root view 
with `getView()`.

```kotlin
class MyShard: Shard() {
    override fun onCreate() {
        setContentView(R.layout.my_shard)
        val name: TextView = requireViewById(R.id.name)
        name.text = "My Name"
    }
}
```

## ViewModel/Lifecycle

A shard is a `ViewModelStoreOwner` and a `LifecycleOwner`. Therefore you can seamlessly use shards
with components that utilize those.

```kotlin
class MyShard: Shard() {
    override fun onCreate() {
        val vm: MyViewModel = ViewModelProviders.of(this).get()
        // listen with LiveData
        vm.name.observe(this, Observer { value -> name.text = value })
    }
}
```

## ActivityCallbacks

Often times you want to interact with activity-level apis. `ActvityCallbacks` allows you to do so in
a composable way. You can access these with `getActivityCallbacks()`. Note: This may not be 
available depending on how you set up your base activity, see
[Getting Started](/docs/getting-started.md).

You can handle back presses

```kotlin
activityCallbacks.addOnBackPrssedCallback { false }
```

, start activities for result

```kotlin
const val REQUEST_CODE = 1

activityCallbacks.addOnActivityResultCallback(REQUEST_CODE) { resultCode, data -> }
requireViewById<Button>(R.id.button).setOnClickListener {
    activityCallbacks.startActivityForResult(Intent(context, MyActivity::class.java), REQUEST_CODE)
}
```

, request permissions

```kotlin
const val REQUEST_CODE = 1

activityCallbacks.addOnRequestPermissionResultCallback(REQUEST_CODE) { permissions, grantResults -> }
requireViewById<Button>(R.id.button).setOnClickListener {
    activityCallbacks.requestPermissions(arrayOf(Manifest.permission.SEND_SMD), REQUEST_CODE)
}
```

, and check in in multi-window/picture-in-picture.

## Saving Instance State

You can save you own custom instance state by using the `SavedStateRegistry`. You can access it with
`getSavedStateRegistry()`.

Add an `SavedStateprovider` and you'll get callbacks to save and restore your state.

```kotlin
const val STATE_KEY = "state"

class MyShard: Shard() {
    override fun onCreate() {
        savedStateRegistry.registerSavedStateProvider(STATE_KEY, object: SavedStateProvider<Bundle> {
            override fun saveState() : Bundle? {
                // save state
                return null
            }

            override fun restoreState(state: Bundle) {
                // restore state
            }
        }) 
    }
}
```

## Args

You can provide args to your shard by putting them in the bundle returned from `getArgs()`.

```kotlin
val shard = MyShard().apply {
    args.putString("name", "My Name")
}
```
