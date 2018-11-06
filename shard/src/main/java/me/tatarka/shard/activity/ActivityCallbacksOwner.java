package me.tatarka.shard.activity;

import androidx.annotation.NonNull;
import me.tatarka.shard.activity.ActivityCallbacks;

public interface ActivityCallbacksOwner {
    @NonNull
    ActivityCallbacks getActivityCallbacks();
}
