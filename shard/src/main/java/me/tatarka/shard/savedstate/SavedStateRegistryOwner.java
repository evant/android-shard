package me.tatarka.shard.savedstate;

import androidx.annotation.NonNull;

public interface SavedStateRegistryOwner {
    @NonNull
    SavedStateRegistry getSavedStateRegistry();
}
