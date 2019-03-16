package me.tatarka.shard.app

import android.os.Bundle

inline fun <reified T : Shard> Shard.Factory.newInstance(args: Bundle = Bundle.EMPTY) =
        newInstance<T>(T::class.java.name, args)
