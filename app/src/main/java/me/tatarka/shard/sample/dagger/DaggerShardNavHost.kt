package me.tatarka.shard.sample.dagger

import android.content.Context
import android.util.AttributeSet
import me.tatarka.shard.nav.ShardNavHost

class DaggerShardNavHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ShardNavHost(context, attrs) {
    init {
        shardFactory = injector.shardFactory
    }
}
