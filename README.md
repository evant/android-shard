# Shard
[WIP] 'Fragments' with a simpler api built on top of the android architecture components

## Usage

Create a shard
```kotlin
class MyShard: Shard() {
    override fun onCreate() {
        // set the layout
        setContentView(R.layout.my_shard)
        // find a view
        val name: TextView = requireViewById(R.id.name)
        // get a ViewModel
        val vm: MyViewModel = ViewModelProviders.of(this).get()
        // listen with LiveData
        vm.name.observe(this, Observer { value -> name.text = value })
    }
}
```

Add a static shard to a layout
```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/host"
    android:name="com.example.MyShard"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Dynamically set a shard
```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/host"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
``` 

```kotlin
requireViewById<ShardHost>(R.id.host).shard = MyShard()
```

BottomNav
```xml
<me.tatarka.shard.wiget.ShardPageHost
    android:id="@+id/host"
    app:startPage="@+id/page1" />

<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_nav"
    app:menu="@menu/bottom_nav" />
```

```kotlin
val host: ShardPageHost = requireViewById(R.id.host)
val bottomNav: BottomNavigationView = requiteViewById(R.id.bottom_nav)
host.adapter = ShardPageHost.Adapter { id ->
    when (id) {
        R.id.page1 -> MyShard1()
        R.id.page2 -> MyShard2()
        R.id.page3 -> MyShard3()
        else -> null
    }
}
ShardPageHostUI.setupWithPageHost(bottomNav, host)
```

Navigation
```xml
<me.tatarka.shard.widget.ShardNavHost
   android:id="@+id/nav"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:graphId="@navigation/nav_graph" />
```

```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    app:startDestination="@id/shard1">

    <shard
        android:id="@+id/shard1"
        android:name="com.example.Shard1"
        android:label="Shard1"
        tools:layout="@layout/shard1" />

    <shard
        android:id="@+id/shard2"
        android:name="com.example.Shard2"
        android:label="Shard2"
        tools:layout="@layout/shard2" />
</navigation>
```

```kotlin
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)
    }
    
    override fun onBackPressed() {
        if (!findNavController(R.id.nav).popBackStack()) {
            super.onBackPressed()
        }
    }

    override fun onNavigateUp(): Boolean = findNavController(R.id.nav).navigateUp()
}
```

ViewPager
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
The current page will be resumed, other pages will be started.

Dialog
```kotlin
class MyDialogShard: DialogShard() {

    override fun onCreateDialog(context: Context): Dialog {
        return Dialog(context, R.style.MyDialogTheme)
    }
    
    override fun onCreate() {
        setContentView(R.layout.dialog_content)
    }
}
```

AlertDialog
```kotlin
class MyAlertDialogShard: AlertDialogShard() {
    override fun onBuildAlertDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(R.string.title)
            .setMessage(R.string.message)
            .setPositiveButton(R.string.ok, null)
    }
    
    override fun onCreate() {
        // Optional
        setContentView(R.layout.custom_dialog_view)
    }
}
```

Show
```kotlin
// make sure to always init this to ensure dialog state is restored on config changes.
val dialogHost = ShardDialogHost(this)

override fun onCreate() {
    setContentView(R.layout.content)
    val button: Button = requireViewById(R.id.button)
    buttion.setOnClickListener {
        dialogHost.show(MyDialogShard())
    }
}
```

Saving/Restoring state
```kotlin
private const val STATE_KEY = "state"

class MyShard: Shard(), InstanceStateSaver<Bundle> {
    override fun onCreate() {
        stateStore.addStateSaver(STATE_KEY, this) 
    }
    
    override fun onSaveInstanceState() : Bundle? {
        // save state
    }

    override fun onRestoreInstanceState(instanceState: Bundle) {
        // restore state
    }
}
```
