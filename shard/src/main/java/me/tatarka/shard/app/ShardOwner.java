package me.tatarka.shard.app;

import android.content.Context;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistryOwner;

import me.tatarka.shard.activity.ActivityCallbacksOwner;
import me.tatarka.shard.content.ComponentCallbacksOwner;

/**
 * Interface that a class that hosts shards must implement. The owner is a {@link LifecycleOwner}
 * a {@link ViewModelStoreOwner}, and a {@link SavedStateRegistryOwner}.See {@link ShardActivity}
 * for a simple implementation.
 */
public interface ShardOwner extends
        OnBackPressedDispatcherOwner,
        ViewModelStoreOwner,
        SavedStateRegistryOwner,
        ActivityCallbacksOwner,
        ComponentCallbacksOwner,
        ShardFactoryProvider {
    @NonNull
    Context getContext();

}
