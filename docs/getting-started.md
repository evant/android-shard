# Getting Started

In order to use shards, you may need to set up your hosting activity. You have a few options here:

1. Do nothing. If you are using a recent version of appcompat, you'll be mostly good-to-go. This is
the easiest option to get started. However, all of
[ActivityCallbacks](/docs/implementing-shards.md#ActivityCallbacks) will not be available except for
`isInMultiWindowMode()` and `isInPictureInPictureMode()`.

2. Subclass `ShardActivity` in the core package or `AppCompatActivity` in `shard-appcompat`. This is
your best option if you don't need to deal with fragments in the same activity. You can even 
completely rid your project of fragments by adding

```groovy
configurations {
    implementation {
        exclude group: 'androidx.fragment', module: 'fragment'
    }
}
```

to your `build.gradle`.

3. Have your base activity implement `ShardOwner`. If you have an existing project using fragments
this is probably the easiest way to go. Just make sure you keep it up to date with any new releases.
You can copy the implementation from
[ShardActivity](/shard/src/main/java/me/tatarka/shard/app/ShardActivity.java).

After setup check out how to [implement shards](/docs/implementing-shards.md) or how to
[host them](/docs/hosting-shards.md).
