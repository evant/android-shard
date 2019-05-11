package me.tatarka.shard.sample

import android.view.View
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.newInstance
import me.tatarka.shard.transition.ShardTransitionCompat
import me.tatarka.shard.wiget.ShardHost
import javax.inject.Inject

class SimpleHostShard @Inject constructor() : Shard(), Resetable {

    lateinit var host: ShardHost

    override fun onCreate() {
        setContentView(R.layout.simple_host)
        host = requireViewById(R.id.host)
        host.defaultTransition =
                ShardTransitionCompat.fromTransitionRes(context, R.transition.square_transition)

        requireViewById<View>(R.id.one).setOnClickListener {
            host.shard = shardFactory.newInstance<ShardSquareLeft>()
        }
        requireViewById<View>(R.id.two).setOnClickListener {
            host.shard = shardFactory.newInstance<ShardSquareRight>()
        }
    }

    override fun reset() {
        host.shard = shardFactory.newInstance<MyShard>()
    }
}
