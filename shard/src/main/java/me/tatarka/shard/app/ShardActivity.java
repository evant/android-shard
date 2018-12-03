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
import me.tatarka.shard.state.InstanceStateStore;

/**
 * A base class for hosting {@link Shard}s. You can either subclass this or duplicate it's
 * implementation into your own base activity.
 */
public class ShardActivity extends ComponentActivity implements ShardOwner {

    private final ShardActivityDelegate delegate = new ShardActivityDelegate(this);

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate(savedInstanceState);
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public InstanceStateStore getInstanceStateStore() {
        return delegate.getInstanceStateStore();
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    @NonNull
    @Override
    public Shard.Factory getShardFactory() {
        return delegate.getShardFactory();
    }

    public void setShardFactory(@NonNull Shard.Factory factory) {
        delegate.setShardFactory(factory);
    }

    @NonNull
    @Override
    public ActivityCallbacks getActivityCallbacks() {
        return delegate.getActivityCallbacks();
    }

    @NonNull
    @Override
    public ComponentCallbacks getComponentCallbacks() {
        return delegate.getComponentCallbacks();
    }

    @Override
    @CallSuper
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        delegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        delegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        delegate.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        delegate.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }
}
