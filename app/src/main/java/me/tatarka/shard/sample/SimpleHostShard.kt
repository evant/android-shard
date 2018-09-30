package me.tatarka.shard.sample

import android.view.View
import me.tatarka.shard.app.Shard
import me.tatarka.shard.sample.dagger.DaggerShardFactory
import me.tatarka.shard.wiget.ShardHost
import javax.inject.Inject

class SimpleHostShard @Inject constructor(
    private val shardFactory: DaggerShardFactory
) : Shard() {

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.simple_host)
        val host: ShardHost = requireViewById(R.id.host)
        requireViewById<View>(R.id.one).setOnClickListener {
            host.shard =
                    shardFactory.newInstance<MyShard>().withNumber(1)
        }
        requireViewById<View>(R.id.two).setOnClickListener {
            host.shard =
                    shardFactory.newInstance<MyShard>().withNumber(2)
        }
    }
}
