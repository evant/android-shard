# Migrating from Fragments

This is a cheat-sheet for equivalent concepts in shard, coming from fragments.

## Views

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
@ContentView(R.layout.my_shard)
class MyShard : Shard() {
    override fun onCreate() {
        requireViewById<TextView>(R.id.name).text = "My Name"
    }
}
```

## Showing Single from Xml

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

## Showing Single from Code

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

## Showing in a Dialog

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
@ContentView(R.layout.dialog)
class MyDialogShard : DialogShard() {
    override fun onCreateDialog(context: Context): Dialog {
        return Dialog(context)
    }
}
```

## Pushing onto BackStack

*Fragment (without navigation component)*
```kotlin
fragmentManager.beginTransaction()
    .replate(R.id.container, MyFragment2())
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