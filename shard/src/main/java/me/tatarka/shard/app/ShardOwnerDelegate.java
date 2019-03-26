package me.tatarka.shard.app;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;

public class ShardOwnerDelegate {

    private final ShardOwner owner;
    private ActivityCallbacksDispatcherFactory activityCallbacksDispatcherFactory;
    private BaseActivityCallbacksDispatcher activityCallbacksDispatcher;
    private ComponentCallbacksDispatcher componentCallbacksDispatcher;
    private Shard.Factory shardFactory = Shard.DefaultFactory.getInstance();

    public ShardOwnerDelegate(ShardOwner owner, ActivityCallbacksDispatcherFactory activityCallbacksDispatcherFactory) {
        this.owner = owner;
        this.activityCallbacksDispatcherFactory = activityCallbacksDispatcherFactory;
    }

    public void onCreate() {
        activityCallbacksDispatcher = activityCallbacksDispatcherFactory.create(owner);
        componentCallbacksDispatcher = new ComponentCallbacksDispatcher(owner);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        activityCallbacksDispatcher.dispatchOnActivityResult(requestCode, resultCode, data);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        activityCallbacksDispatcher.dispatchOnRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        activityCallbacksDispatcher.dispatchOnMultiWindowModeChanged(isInMultiWindowMode);
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        activityCallbacksDispatcher.dispatchOnMultiWindowModeChanged(isInPictureInPictureMode);
    }

    @NonNull
    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbacksDispatcher;
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

    public interface ActivityCallbacksDispatcherFactory {
        BaseActivityCallbacksDispatcher create(ShardOwner owner);
    }
}
