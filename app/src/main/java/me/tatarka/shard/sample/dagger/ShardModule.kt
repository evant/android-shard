package me.tatarka.shard.sample.dagger

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tatarka.shard.app.Shard
import me.tatarka.shard.sample.*

@Module
abstract class ShardModule {

    @Binds
    internal abstract fun factory(factory: DaggerShardFactory): Shard.Factory

    @Binds
    @IntoMap
    @ShardKey(MyShard::class)
    internal abstract fun myShard(shard: MyShard): Shard

    @Binds
    @IntoMap
    @ShardKey(ShardSquareLeft::class)
    internal abstract fun shardSquareLeft(shard: ShardSquareLeft): Shard

    @Binds
    @IntoMap
    @ShardKey(ShardSquareMiddle::class)
    internal abstract fun shardSquareMiddle(shard: ShardSquareMiddle): Shard

    @Binds
    @IntoMap
    @ShardKey(ShardSquareRight::class)
    internal abstract fun shardSquareRight(shard: ShardSquareRight): Shard

    @Binds
    @IntoMap
    @ShardKey(SimpleHostShard::class)
    internal abstract fun simpleHostShard(shard: SimpleHostShard): Shard

    @Binds
    @IntoMap
    @ShardKey(NavigationShard::class)
    internal abstract fun navigationShard(shard: NavigationShard): Shard

    @Binds
    @IntoMap
    @ShardKey(ViewPagerShard::class)
    internal abstract fun viewPagerShard(shard: ViewPagerShard): Shard

    @Binds
    @IntoMap
    @ShardKey(DialogHostShard::class)
    internal abstract fun dialogHostShard(shard: DialogHostShard): Shard

    @Binds
    @IntoMap
    @ShardKey(MyAlertDialogShard::class)
    internal abstract fun myAlertDialogShard(shard: MyAlertDialogShard): Shard

    @Binds
    @IntoMap
    @ShardKey(SimpleDialogShard::class)
    internal abstract fun simpleDialogShard(shard: SimpleDialogShard): Shard
}
