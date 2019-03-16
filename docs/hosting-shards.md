# Hosting Shards

In order to show a shard in the UI, you need to use a 'host'. Various hosts have different features
depending on what you want to do.

## ShardHost

`ShardHost` is the simplest host. If will show your shard in your view hierarchy. You can either
define the shard you want to show in xml,

```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/host"
    android:name="com.example.MyShard"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

or set it dynamically in code.

```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/host"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
requireViewById<ShardHost>(R.id.host).shard = MyShard()
```
Note that what shard is set and it's state is automatically saved. Setting a new shard will destroy
the old one and it's state will be lost.

## ShardPageHost

`ShardPageHost` allows you to select a shared based on an id. It is useful for implementing nav 
drawer or bottom nav navigation. State of all shards will be saved and restored as you navigate 
between them.

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android">
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <me.tatarka.shard.wiget.ShardPageHost
        android:id="@+id/host"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        app:startPage="@+id/page1" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/bottom_nav"
        app:menu="@menu/bottom_nav" />
</LinearLayout>
```

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/page1"
        android:title="Page 1" />

    <item
        android:id="@+id/page2"
        android:title="Page 2" />

    <item
        android:id="@+id/page3"
        android:title="Page 3" />
</menu>
```

```kotlin
val host: ShardPageHost = requireViewById(R.id.host)
val bottomNav: BottomNavigationView = requiteViewById(R.id.bottom_nav)
host.adapter = ShardPageHost.Adapter { id ->
    when (id) {
        R.id.page1 -> MyShard1()
        R.id.page2 -> MyShard2()
        R.id.page3 -> MyShard3()
        else -> throw AssertionError()
    }
}
// this is in shard-host-ui as it depends on the material design lib
ShardPageHostUI.setupWithPageHost(bottomNav, host)
```

If you want to use `ShardPageHost` with something else, you can wire it up manually using 
`host.setOnPageChangedListener()` to listen to page changes and `host.setCurrentPage()` to set the
current page.

## ShardDialogHost

`ShardDialogHost` will display a shard in a dialog.

First, have your shard subclass `DialogShard` 

```kotlin
class MyDialogShard: DialogShard() {

    override fun onCreateDialog(context: Context): Dialog {
        return Dialog(context, R.style.MyDialogTheme)
    }
    
    override fun onCreate() {
        // Sets the content view of the dialog
        setContentView(R.layout.dialog_content)
    }
}
```

or `AlertDialogShard` (there's a compat version of `AlertDialogShard` in `shard-appcompat`)

```kotlin
class MyAlertDialogShard: AlertDialogShard() {
    override fun onBuildAlertDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(R.string.title)
            .setMessage(R.string.message)
            .setPositiveButton(R.string.ok, null)
    }
    
    override fun onCreate() {
        // Optional, sets the content view of the dialog
        setContentView(R.layout.custom_dialog_view)
    }
}
```

Then, where you want to show it call `showDialog(shard)`.

```kotlin
override fun onCreate() {
    setContentView(R.layout.content)
    val button: Button = requireViewById(R.id.button)
    buttion.setOnClickListener {
        showDialog(MyDialogShard())
    }
}
```

You can manually dismiss the dialog with `DialogShard.dismiss()`. If you need to listen to
`onCancel()` or `onDismiss()` callbacks, you must do this by overriding the relevant method in the 
shard. The callbacks on the dialog itself will get overwritten by the `ShardDialogHost` 
implementation.

## ShardNavHost

`ShardNavHost` allows you to navigate to different destinations using the androidx 
[navigation component](https://developer.android.com/topic/libraries/architecture/navigation/). It 
is defined in `shard-nav`.

```xml
<me.tatarka.shard.widget.ShardNavHost
   android:id="@+id/nav"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:navGraph="@navigation/nav_graph" />
```

Use the `<shard/>` tag in your navigation graph to define a shard as a navigation destination.

```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    app:startDestination="@id/shard1">

    <shard
        android:id="@+id/shard1"
        android:name="com.example.Shard1"
        android:label="Shard1"
        tools:layout="@layout/shard1">
            
        <action
            android:id="@+id/to_shard2"
            app:destination="@id/shard2"
            app:launchSingleTop="true"
            app:enterAnim="@anim/fade_in"
            app:exitAnim="@anim/fade_out" />
    </shard>

    <shard
        android:id="@+id/shard2"
        android:name="com.example.Shard2"
        android:label="Shard2"
        tools:layout="@layout/shard2">
        
        <argument
            android:name="number"
            android:defaultValue="1"
            app:argType="int" />
    </shard>
</navigation>
```

You can then navigate to a shard just like anything else.

```kotlin
val controller = findNavController(view)
controler.navigate(R.id.to_shard2, Bundle().apply {
    putInt("number", 2)
})
```

In addition to the built-in animation options defined in xml, you can set a transition using the 
extras.

```kotlin
controler.navigate(R.id.to_dest, null, null, ShardNavigator.Extras.Builder()
    .transition(R.transition.my_transition)
    .build())
```

## ShardPagerAdapter

`ShardPagerAdapter` hosts shards in a `ViewPager`. It is defined in `shard-pager`.

```kotlin
val pager: ViewPager = requireViewById(R.id.pager)
pager.adapter = object : ShardPagerAdapter(this) {
    override fun getItem(position: Int): Shard {
        return when (position) {
            case 0 -> MyShard1()
            case 1 -> MyShard2()
            case 2 -> MyShard3()
            else -> throw AssertionError()
        }
    }

    override fun getCount(): Int = 3
}
```

Shards in a `ViewPager` have a custom lifecycle. The current shard will be in the resumed state, any
other shards who's views are present will be in the started state. When a page is destroyed it's 
shard will be as well, however the state will be saved and restored. By default the list of shards 
is not expected to change. You can, however override `getItemPosition()` to support changing the 
pages.

## Custom Host

All hosts above are built on the lower-level `ShardManager` api. You can use this directly to build
custom hosts for your own use-cases. The lifecycle is as follows:

![ShardManager Lifecycle](/docs/shardmanager-lifecycle.svg)

You add a shard to a ViewGroup with `add()`, and remove it with `remove()`. To properly handle
instance state, you call `saveState()` which will return you some parcelable state to save. You then 
call `restoreState()` before you add the shard again with `add()` to have it restore that state. You 
then call `remove()` to remove the shard. The shard's viewmodels will stay around until you call
`remove()`. You can also use `replace()` to remove one shard from a container and add another one,
optionally providing a transition animation to run.
