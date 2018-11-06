package me.tatarka.shard.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.state.InstanceStateStore;

public class ShardActivity extends ComponentActivity implements ShardOwner {

    private static final String STATE_SHARD = "me.tatarka.shard.app.Shard";

    private final InstanceStateRegistry stateStore = new InstanceStateRegistry();
    private ActivityCallbacksDispatcher activityCallbackDispatcher;
    private ComponentCallbacksDispatcher componentCallbacksDispatcher;
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
        activityCallbackDispatcher = new ActivityCallbacksDispatcher(this);
        componentCallbacksDispatcher = new ComponentCallbacksDispatcher(this);
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(STATE_SHARD, stateStore.onSaveInstanceState());
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
    public boolean onNavigateUp() {
        return activityCallbackDispatcher.dispatchOnNavigateUp() || super.onNavigateUp();
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
