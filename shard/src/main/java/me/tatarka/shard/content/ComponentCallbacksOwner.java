package me.tatarka.shard.content;

import androidx.annotation.NonNull;

public interface ComponentCallbacksOwner {
    @NonNull
    ComponentCallbacks getComponentCallbacks();
}
