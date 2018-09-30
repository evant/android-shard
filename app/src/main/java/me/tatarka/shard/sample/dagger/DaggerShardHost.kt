package me.tatarka.shard.sample.dagger

import android.content.Context
import android.util.AttributeSet

import me.tatarka.shard.wiget.ShardHost

class DaggerShardHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ShardHost(context, attrs) {
    init {
        shardFactory = injector.shardFactory
    }
}
