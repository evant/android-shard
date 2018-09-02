# better-fragment
[WIP] 'Fragments' with a simpler api built on top of the android architecture components

## Usage

Create a fragment
```kotlin
class MyFragment: Fragment() {
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        // set the layout
        setContentView(R.layout.my_fragment)
        // find a view
        val name: TextView = requireViewById(R.id.name)
        // get a ViewModel
        val vm: MyViewModel = ViewModelProviders.of(this).get()
        // listen with LiveData
        vm.name.observe(this, Observer { value -> name.text = value })
    }
    
    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        // save state
    }
}
```

Add a static fragment to a layout
```xml
<me.tatarka.betterfragment.widget.FragmentHost
    android:id="@+id/host"
    android:name="com.example.MyFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Dynamically set a fragment
```xml
<me.tatarka.betterfragment.widget.FragmentHost
    android:id="@+id/host"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
``` 

```kotlin
requireViewById<FragmentHost>(R.id.host).fragment = MyFragment()
```

BottomNav
```xml
<me.tatarka.betterfragment.widget.FragmentPageHost
    android:id="@+id/host"
    app:startPage="@+id/page1" />

<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_nav"
    app:menu="@menu/bottom_nav" />
```

```kotlin
val host: FragmentPageHost = requireViewById(R.id.host)
val bottomNav: BottomNavigationView = requiteViewById(R.id.bottom_nav)
host.adapter = FragmentPageHost.Adapter { id ->
    when (id) {
        R.id.page1 -> MyFragment1()
        R.id.page2 -> MyFragment2()
        R.id.page3 -> MyFragment3()
        else -> null
    }
}
FragmentPageHostUI.setupWithPageHost(bottomNav, host)
```

Navigation
```xml
<me.tatarka.betterfragment.widget.FragmentNavHost
   android:id="@+id/nav"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   app:graphId="@navigation/nav_graph" />
```

```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    app:startDestination="@id/fragment1">

    <fragment
        android:id="@+id/fragment1"
        android:name="com.example.Fragment1"
        android:label="Fragment1"
        tools:layout="@layout/fragment1" />

    <fragment
        android:id="@+id/fragment2"
        android:name="com.example.Fragment2"
        android:label="Fragment2"
        tools:layout="@layout/fragment2" />
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
pager.adapter = object : FragmentPagerAdapter(this) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            case 0 -> MyFragment1()
            case 1 -> MyFragment2()
            case 2 -> MyFragment3()
            else -> throw AssertionError()
        }
    }

    override fun getCount(): Int = 3
}
```
The current page will be resumed, other pages will be started.
