package me.tatarka.shard.app;

import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;

/**
 * Helper to implement a {@link ShardActivity}
 */
public final class ShardActivityDelegate {

    private final ComponentActivity activity;
    private ActivityCallbacksDispatcher activityCallbackDispatcher;
    private ComponentCallbacksDispatcher componentCallbacksDispatcher;
    private Shard.Factory shardFactory = Shard.DefaultFactory.getInstance();

    public <A extends ComponentActivity & ShardOwner> ShardActivityDelegate(A activity) {
        this.activity = activity;
    }

    public void onCreate() {
        activityCallbackDispatcher = new ActivityCallbacksDispatcher(activity);
        componentCallbacksDispatcher = new ComponentCallbacksDispatcher((ShardOwner) activity);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        activityCallbackDispatcher.dispatchOnActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        activityCallbackDispatcher.dispatchOnRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        activityCallbackDispatcher.dispatchOnMultiWindowModeChanged(isInMultiWindowMode);
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        activityCallbackDispatcher.dispatchOnMultiWindowModeChanged(isInPictureInPictureMode);
    }

    @NonNull
    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbackDispatcher;
    }

    @NonNull
    public Shard.Factory getShardFactory() {
        return shardFactory;
    }

    public void setShardFactory(@NonNull Shard.Factory factory) {
        this.shardFactory = factory;
    }

    @NonNull
    public ComponentCallbacks getComponentCallbacks() {
        return componentCallbacksDispatcher;
    }
}
