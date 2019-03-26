package me.tatarka.shard.fragment.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import me.tatarka.shard.activity.ActivityCallbacksOwner;
import me.tatarka.shard.app.BaseActivityCallbacksDispatcher;
import me.tatarka.shard.app.ShardActivity;

/**
 * Helps with implementing {@link ActivityCallbacksOwner}. Will dispatch  activity callbacks to any
 * registered listeners.
 *
 * @see ShardActivity for an example of how to use.
 */
public final class FragmentActivityCallbacksDispatcher extends BaseActivityCallbacksDispatcher {

    private final Fragment fragment;

    public FragmentActivityCallbacksDispatcher(Fragment fragment) {
        super(fragment);
        this.fragment = fragment;
    }

    @Override
    public void addOnBackPressedCallback(LifecycleOwner owner, OnBackPressedCallback callback) {
        fragment.requireActivity().addOnBackPressedCallback(owner, callback);
    }

    @Override
    public void removeOnBackPressedCallback(OnBackPressedCallback callback) {
        fragment.requireActivity().removeOnBackPressedCallback(callback);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        fragment.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void requestPermissions(@NonNull String[] permissions, int requestCode) {
        fragment.requestPermissions(permissions, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && fragment.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public boolean isInMultiWindowMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && fragment.requireActivity().isInMultiWindowMode();
    }

    @Override
    public boolean isInPictureInPictureMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && fragment.requireActivity().isInPictureInPictureMode();
    }
}
