package me.tatarka.shard.app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import me.tatarka.shard.activity.ActivityCallbacksOwner;

/**
 * Helps with implementing {@link ActivityCallbacksOwner}. Will dispatch  activity callbacks to any
 * registered listeners.
 *
 * @see ShardActivity for an example of how to use.
 */
public final class ActivityCallbacksActivityDispatcher extends ActivityCallbacksDispatcher {

    private final ComponentActivity activity;

    public ActivityCallbacksActivityDispatcher(ComponentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        activity.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public void startIntentSenderForResult(@NonNull IntentSender intent, int requestCode,
                                           @Nullable Intent fillIntent, int flagsMask, int flagsValues, int extraFlags,
                                           Bundle options) throws IntentSender.SendIntentException {
        activity.startIntentSenderForResult(intent, requestCode, fillIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    public void requestPermissions(@NonNull String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        } else {
            // assume granted
            dispatchOnRequestPermissionsResult(requestCode, permissions, new int[permissions.length]);
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public boolean isInMultiWindowMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode();
    }

    @Override
    public boolean isInPictureInPictureMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInPictureInPictureMode();
    }
}
