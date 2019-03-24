package me.tatarka.shard.app

inline fun <reified T : Shard> Shard.Factory.newInstance() = newInstance<T>(T::class.java.name)
