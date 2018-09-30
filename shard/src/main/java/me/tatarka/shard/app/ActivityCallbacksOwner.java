package me.tatarka.shard.app;

import androidx.annotation.NonNull;

public interface ActivityCallbacksOwner {
    @NonNull
    ActivityCallbacks getActivityCallbacks();
}
