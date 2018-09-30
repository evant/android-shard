package me.tatarka.shard.sample.dagger

import dagger.MapKey
import me.tatarka.shard.app.Shard
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@MapKey
@Retention(RUNTIME)
annotation class ShardKey(val value: KClass<out Shard>)