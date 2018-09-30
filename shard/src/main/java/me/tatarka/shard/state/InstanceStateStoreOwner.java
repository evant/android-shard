package me.tatarka.shard.state;

import androidx.annotation.NonNull;

public interface InstanceStateStoreOwner {
    @NonNull
    InstanceStateStore getInstanceStateStore();
}
