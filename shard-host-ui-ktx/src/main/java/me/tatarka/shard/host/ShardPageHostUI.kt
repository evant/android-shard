@file:Suppress("NOTHING_TO_INLINE")

package me.tatarka.shard.host

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import me.tatarka.shard.wiget.ShardPageHost

inline fun BottomNavigationView.setupWithPageHost(host: ShardPageHost) {
    ShardPageHostUI.setupWithPageHost(this, host)
}

inline fun NavigationView.setupWithPageHost(host: ShardPageHost) {
    ShardPageHostUI.setupWithPageHost(this, host)
}
