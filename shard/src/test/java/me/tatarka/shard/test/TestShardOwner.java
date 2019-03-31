package me.tatarka.shard.test;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.test.platform.app.InstrumentationRegistry;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.ActivityCallbacksActivityDispatcher;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;

public class TestShardOwner implements ShardOwner {

    final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    final ViewModelStore viewModelStore = new ViewModelStore();
    final SavedStateRegistryController savedStateRegistryController = SavedStateRegistryController.create(this);
    final ActivityCallbacks activityCallbacks;
    final ComponentCallbacks componentCallbacks = new ComponentCallbacksDispatcher(this);

    public TestShardOwner(ComponentActivity activity) {
        activityCallbacks = new ActivityCallbacksActivityDispatcher(activity);
    }

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
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @NonNull
    @Override
    public Shard.Factory getShardFactory() {
        return Shard.DefaultFactory.getInstance();
    }

    @NonNull
    @Override
    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbacks;
    }

    @NonNull
    @Override
    public ComponentCallbacks getComponentCallbacks() {
        return componentCallbacks;
    }

    @NonNull
    @Override
    public SavedStateRegistry getSavedStateRegistry() {
        return savedStateRegistryController.getSavedStateRegistry();
    }
}
