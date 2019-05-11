package me.tatarka.shard.fragment.app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import me.tatarka.shard.activity.ActivityCallbacksOwner;
import me.tatarka.shard.app.ActivityCallbacksDispatcher;
import me.tatarka.shard.app.ShardActivity;

/**
 * Helps with implementing {@link ActivityCallbacksOwner}. Will dispatch  activity callbacks to any
 * registered listeners.
 *
 * @see ShardActivity for an example of how to use.
 */
public final class ActivityCallbacksFragmentDispatcher extends ActivityCallbacksDispatcher {

    private final Fragment fragment;

    public ActivityCallbacksFragmentDispatcher(Fragment fragment) {
        super(fragment);
        this.fragment = fragment;
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
    public void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        fragment.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Override
    public void startIntentSenderForResult(@NonNull IntentSender intent, int requestCode,
                                           @Nullable Intent fillIntent, int flagsMask, int flagsValues, int extraFlags,
                                           Bundle options) throws IntentSender.SendIntentException {
        fragment.startIntentSenderForResult(intent, requestCode, fillIntent, flagsMask, flagsValues, extraFlags, options);
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
