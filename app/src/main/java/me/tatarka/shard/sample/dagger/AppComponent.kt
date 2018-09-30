package me.tatarka.shard.sample.dagger

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ShardModule::class])
interface AppComponent {
    val shardFactory: DaggerShardFactory
}

val injector: AppComponent = DaggerAppComponent.create()
