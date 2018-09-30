package me.tatarka.shard.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.state.InstanceStateStore;

public class ShardActivity extends Activity implements ShardOwner {

    private static final String STATE_SHARD = "me.tatarka.shard.app.Shard";

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final InstanceStateRegistry stateStore = new InstanceStateRegistry();
    private final ActivityCallbackDispatcher activityCallbackDispatcher = new ActivityCallbackDispatcher(this);
    private final ComponentCallbacksDispatcher componentCallbacksDispatcher = new ComponentCallbacksDispatcher();
    private ViewModelStore viewModelStore;
    private boolean isRetaining;
    private Shard.Factory factory = Shard.DefaultFactory.getInstance();

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle state = savedInstanceState.getBundle(STATE_SHARD);
            if (state != null) {
                stateStore.onRestoreInstanceState(state);
            }
        }
        registerComponentCallbacks(componentCallbacksDispatcher);
        viewModelStore = (ViewModelStore) getLastNonConfigurationInstance();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        super.onSaveInstanceState(outState);
        outState.putBundle(STATE_SHARD, stateStore.onSaveInstanceState());
    }

    @Override
    public final Object onRetainNonConfigurationInstance() {
        isRetaining = true;
        return viewModelStore;
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        if (viewModelStore != null && !isRetaining) {
            viewModelStore.clear();
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        unregisterComponentCallbacks(componentCallbacksDispatcher);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
        }
        return viewModelStore;
    }

    @NonNull
    @Override
    public InstanceStateStore getInstanceStateStore() {
        return stateStore;
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    @NonNull
    @Override
    public Shard.Factory getShardFactory() {
        return factory;
    }

    @NonNull
    @Override
    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbackDispatcher;
    }

    @NonNull
    @Override
    public ComponentCallbacks getComponentCallbacks() {
        return componentCallbacksDispatcher;
    }

    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        activityCallbackDispatcher.dispatchOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        activityCallbackDispatcher.dispatchOnRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        activityCallbackDispatcher.dispatchOnMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        activityCallbackDispatcher.dispatchOnMultiWindowModeChanged(isInPictureInPictureMode);
    }
}
