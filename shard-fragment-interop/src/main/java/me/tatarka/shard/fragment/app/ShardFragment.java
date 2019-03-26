package me.tatarka.shard.fragment.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.content.ComponentCallbacks;

/**
 * A {@link Fragment} that can hosts shards.
 */
public class ShardFragment extends Fragment implements ShardOwner {

    private final ShardFragmentDelegate delegate = new ShardFragmentDelegate(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate();
    }

    @NonNull
    @Override
    public Context getContext() {
        return super.requireContext();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
