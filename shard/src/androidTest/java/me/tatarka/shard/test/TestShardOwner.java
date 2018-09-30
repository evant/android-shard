package me.tatarka.shard.test;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.test.InstrumentationRegistry;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.state.InstanceStateStore;

public class TestShardOwner implements ShardOwner {

    final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    final ViewModelStore viewModelStore = new ViewModelStore();
    final InstanceStateRegistry stateStore = new InstanceStateRegistry();

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    @NonNull
    @Override
    public Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    @NonNull
    @Override
    public InstanceStateStore getInstanceStateStore() {
        return stateStore;
    }
}
