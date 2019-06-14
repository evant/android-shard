# Migrating from Fragments

## Cheat-Sheet

This is a cheat-sheet for equivalent concepts in shard, coming from fragments.

### Views

*Fragment*
```kotlin
class MyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInlatter, parent: ViewGroup, savedInstanceState: Bundle?)
        = inflater.inflate(R.layout.my_fragment, parent, false)
        
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view.requireViewById<TextView>(R.id.name).text = "My Name"
    }
}
```

*Shard*
```kotlin
class MyShard : Shard() {
    override fun onCreate() {
        setContentView(R.layout.my_shard)
        requireViewById<TextView>(R.id.name).text = "My Name"
    }
}
```

### Showing Single from Xml

*Fragment*
```xml
<fragment
    android:id="@+id/my_fragment"
    andriod:name="com.example.MyFragment" />
```

*Shard*
```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/my_shard"
    android:name="com.example.MyShard" />
```

### Showing Single from Code

*Fragment*
```xml
<FrameLayout
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
if (savedInstanceState == null) {
    fragmentManager.beginTransaction()
        .add(R.id.container, MyFragment())
        .commit()
}
```

*Shard*
```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
val container = requireViewById<ShardHost>(R.id.container)
if (container.shard == null) {
    container.shard = MyShard()
}
```

### Showing in a Dialog

*Fragment*
```kotlin
class MyDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(activity, theme).apply {
            setContentView(R.layout.dialog)
        }
    }
}
```

```kotlin
button.setOnClickListener {
    MyDialogFragment().show(fragmentManager, "DIALOG_TAG")
}
```

*Shard*
```kotlin
class MyDialogShard : DialogShard() {
    override fun onCreate() {
        setContentView(R.layout.dialog)
    }

    override fun onCreateDialog(context: Context): Dialog {
        return Dialog(context)
    }
}
```

### Pushing onto BackStack

*Fragment (without navigation component)*
```kotlin
fragmentManager.beginTransaction()
    .replace(R.id.container, MyFragment2())
    .addToBackStack(null)
    .commit()
```

*Fragment (with navigation component)*
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/my_fragment1">

    <fragment
        android:id="@+id/my_fragment2"
        android:name="com.example.MyFragment2" />
</navigation>
```

```xml
<fragment
    android:id="@+id/nav_host"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/my_graph" />
```

```kotlin
findNavController(view).navigate(R.id.my_fragment2)
```

*Shard (with shard-backstack)*
```xml
<me.tatarka.shard.backstack.ShardBackStackHost
   android:id="@+id/container"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:startingShard="com.example.MyShard1" />
```

```kotlin
val host: ShardBackStackHost = findViewById(R.id.container)
host.backStack.push(MyShard2())
```

*Shard (with navigation component)*
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/my_shard1">

    <shard
        android:id="@+id/my_shard2"
        android:name="com.example.MyShard2" />
</navigation>
```

```xml
<me.tatarka.shard.widget.ShardNavHost
   android:id="@+id/nav_host"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:navGraph="@navigation/my_graph" />
```

```kotlin
findNavController(view).navigate(R.id.my_shard2)
```

## Interop

When migrating an app to shards, it may be difficult to do all at once. The fragment-interop 
artifact is available to make this easier. It allows you to host shards in fragments and fragments 
in shards.

### Shards in Fragments

You can either make your fragment extend `ShardFragment` or implement it yourself with 
`ShardFragmentDelegate`. You can then host shards in a fragment much like you can in an activity.
The delegate works much the same way as `ShardActivityDelegate` except you must also override
`getContext()` and `onGetLayoutInfater()`. This is so the fragment `ShardOwner` can be obtained from a context.

### Fragments in Shards

Instead of extending `ShardActivity`/`ShardAppCompatActivity`, extend `ShardFragmentActivity`/`ShardFragmentAppCompatActivity`.
You can then obtain a child fragment manager in a shard with `ShardFragmentManager.getFragmentManager(shard)`.
If if can't extend, you can implement your base activity with `ShardActivityDelegate` much like `ShardActivity`.
The only extra thing you must do is wrap the result of `getShardFactory()` with `ShardFragmentManager.wrapFactory()`.
This is required for `<fragment/>` tag support in layout files.
