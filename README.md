# Shard

Shards are 'Fragments' with a simpler api built on top of the android architecture components. 

**Stability**: All important features are implemented but there may be some api changes before `1.0.0`.

## Download
[![Maven Central](https://img.shields.io/maven-central/v/me.tatarka.shard/shard.svg)](https://search.maven.org/search?q=g:me.tatarka.shard)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/me.tatarka.shard/shard.svg)](https://oss.sonatype.org/content/repositories/snapshots/me/tatarka/shard/)

```groovy
def shard_version = '1.0.0-beta01'
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
// For interop with fragments
implementation "me.tatarka.shard:shard-fragment-interop:$shard_version"
```

### Kotlin

There's a few additional artifacts for kotlin extensions.

```groovy
implementation "me.tatarka.shard:shard-ktx:$shard_version"
implementation "me.tatarka.shard:shard-host-ui-ktx:$shard_version"
```

### SNAPSHOT

You can also follow the bleeding-edge with SNAPSHOT releases.
```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}
def shard_version = '1.0.0-beta02-SNAPSHOT'
```

## Usage

Creating a shard is as simple as
```kotlin
class MyShard: Shard() {
    const val REQUEST_CODE = 1

    override fun onCreate() {
        setContentView(R.layout.my_shard)
        // find a view
        val name: TextView = requireViewById(R.id.name)
        // get a ViewModel
        val vm: MyViewModel = ViewModelProviders.of(this).get()
        // listen with LiveData
        vm.name.observe(this, Observer { value -> name.text = value })
        // handle back presses
        onBackPressedDispatcher.addCallback(this, enabled = true) {
            remove()
        }
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
    android:layout_height="match_parent"
    tools:layout="@layout/my_shard" />
```

## Full Documentation

- [Getting Started](/docs/getting-started.md)
- [Implementing Shards](/docs/implementing-shards.md)
- [Hosting Shards](/docs/hosting-shards.md)
- [Transition Animations](/docs/transition-animations.md)
- [Migrating from Fragments](/docs/migrating-from-fragments.md)
