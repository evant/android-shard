package me.tatarka.shard.sample.dagger

import me.tatarka.shard.app.Shard
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerShardFactory @Inject constructor(
    shardMap: @JvmSuppressWildcards Map<Class<out Shard>, Provider<Shard>>
) : Shard.Factory {

    private val shardMap = shardMap.mapKeys { it.key.name }

    override fun <T : Shard> newInstance(name: String): T =
        shardMap.getValue(name).get() as T
}
