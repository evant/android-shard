package me.tatarka.shard.sample

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import me.tatarka.shard.app.Shard
import me.tatarka.shard.host.ShardPageHostUI
import me.tatarka.shard.wiget.ShardPageHost

inline fun <reified T : Shard> Shard.Factory.newInstance(args: Bundle = Bundle.EMPTY) =
    newInstance<T>(T::class.java.name, args)

inline fun BottomNavigationView.setupWithPageHost(host: ShardPageHost) {
    ShardPageHostUI.setupWithPageHost(this, host)
}

inline fun NavigationView.setupWithPageHost(host: ShardPageHost) {
    ShardPageHostUI.setupWithPageHost(this, host)
}