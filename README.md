# Shard

Shards are 'Fragments' with a simpler api built on top of the android architecture components. 

**Stability**: All important features are implemented but there may be some api changes before `1.0.0`.

## Download
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.tatarka.shard/shard/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/me.tatarka.shard/shard)

```groovy
def shard_version = '1.0.0-alpha03'
// The core lib
implementation "me.tatarka.shard:shard:$shard_version"
// For use with appcompat
implementation "me.tatarka.shard:shard-appcompat:$shard_version"
// For use with the material design lib 
implementation "me.tatarka.shard:shard-host-ui:$shard_version"
// For use with the android architecture navigation component
implementation "me.tatarka.shard:shard-nav:$shard_version"
// For use in a ViewPager
implementation "me.tatarka.shard:shard-pager:$shard_version"
// For using androidx.transition
implementation "me.tatarka.shard:shard-transition:$shard_version"
```

## Usage

Creating a shard is as simple as
```kotlin
@ContentView(R.layout.my_shard)
class MyShard: Shard() {
    const val REQUEST_CODE = 1

    override fun onCreate() {
        // find a view
        val name: TextView = requireViewById(R.id.name)
        // get a ViewModel
        val vm: MyViewModel = ViewModelProviders.of(this).get()
        // listen with LiveData
        vm.name.observe(this, Observer { value -> name.text = value })
        // handle back presses
        activityCallbacks.addOnBackPrssedCallback { false }
        // start an activity for result
        activityCallbacks.addOnActivityResultCallback(REQUEST_CODE) { resultCode, data -> }
        requireViewById<Button>(R.id.button).setOnClickListener {
            activityCallbacks.startActivityForResult(Intent(context, MyActivity::class.java), REQUEST_CODE)
        }
    }
}
```

```xml
<me.tatarka.shard.wiget.ShardHost
    android:id="@+id/host"
    android:name="com.example.MyShard"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

## Full Documentation

- [Getting Started](/docs/getting-started.md)
- [Implementing Shards](/docs/implementing-shards.md)
- [Hosting Shards](/docs/hosting-shards.md)
- [Transition Animations](/docs/transition-animations.md)
- [Migrating from Fragments](/docs/migrating-from-fragments.md)
