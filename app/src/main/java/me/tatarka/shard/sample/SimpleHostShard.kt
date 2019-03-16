package me.tatarka.shard.sample

import android.view.View
import androidx.annotation.ContentView
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.newInstance
import me.tatarka.shard.transition.ShardTransitionCompat
import me.tatarka.shard.wiget.ShardHost
import javax.inject.Inject

@ContentView(R.layout.simple_host)
class SimpleHostShard @Inject constructor() : Shard() {

    override fun onCreate() {
        val host: ShardHost = requireViewById(R.id.host)
        host.defaultTransition =
                ShardTransitionCompat.fromTransitionRes(context, R.transition.square_transition)

        requireViewById<View>(R.id.one).setOnClickListener {
            host.shard = shardFactory.newInstance<ShardSquareLeft>()
        }
        requireViewById<View>(R.id.two).setOnClickListener {
            host.shard = shardFactory.newInstance<ShardSquareRight>()
        }
    }
}
