package me.tatarka.shard.app;

import androidx.annotation.NonNull;

public interface ShardFactoryProvider {
    @NonNull
    Shard.Factory getShardFactory();
}
