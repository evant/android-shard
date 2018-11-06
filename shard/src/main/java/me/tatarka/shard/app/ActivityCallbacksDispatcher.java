package me.tatarka.shard.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import me.tatarka.shard.activity.OnNavigateUpCallback;
import me.tatarka.shard.app.BaseActivityCallbacksDispatcher;

public final class ActivityCallbacksDispatcher extends BaseActivityCallbacksDispatcher {

    private final ComponentActivity activity;
    private final CopyOnWriteArrayList<LifecycleAwareOnNavigateUpCallback> onNavigateUpCallbacks =
            new CopyOnWriteArrayList<>();

    public ActivityCallbacksDispatcher(ComponentActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void addOnBackPressedCallback(LifecycleOwner owner, OnBackPressedCallback callback) {
        activity.addOnBackPressedCallback(owner, callback);
    }

    @Override
    public void removeOnBackPressedCallback(OnBackPressedCallback callback) {
        activity.removeOnBackPressedCallback(callback);
    }

    public final boolean dispatchOnNavigateUp() {
        for (LifecycleAwareOnNavigateUpCallback callback : onNavigateUpCallbacks) {
            if (callback.handleOnNavigateUp()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addOnNavigateUpCallback(LifecycleOwner owner, OnNavigateUpCallback callback) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            // Already destroyed, nothing to do
            return;
        }
        // Add new callbacks to the front of the list so that
        // the most recently added callbacks get priority
        onNavigateUpCallbacks.add(0, new LifecycleAwareOnNavigateUpCallback(
                owner.getLifecycle(), callback));
    }

    @Override
    public void removeOnNavigateUpCallback(OnNavigateUpCallback onNavigateUpCallback) {
        Iterator<LifecycleAwareOnNavigateUpCallback> iterator =
                onNavigateUpCallbacks.iterator();
        LifecycleAwareOnNavigateUpCallback callbackToRemove = null;
        while (iterator.hasNext()) {
            LifecycleAwareOnNavigateUpCallback callback = iterator.next();
            if (callback.getOnNavigateUpCallback().equals(onNavigateUpCallback)) {
                callbackToRemove = callback;
                break;
            }
        }
        if (callbackToRemove != null) {
            callbackToRemove.onRemoved();
            onNavigateUpCallbacks.remove(callbackToRemove);
        }
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

    private class LifecycleAwareOnNavigateUpCallback implements
            OnNavigateUpCallback,
            GenericLifecycleObserver {
        private final Lifecycle lifecycle;
        private final OnNavigateUpCallback onNavigateUpCallback;

        LifecycleAwareOnNavigateUpCallback(@NonNull Lifecycle lifecycle,
                                           @NonNull OnNavigateUpCallback onBackPressedCallback) {
            this.lifecycle = lifecycle;
            onNavigateUpCallback = onBackPressedCallback;
            this.lifecycle.addObserver(this);
        }

        Lifecycle getLifecycle() {
            return lifecycle;
        }

        OnNavigateUpCallback getOnNavigateUpCallback() {
            return onNavigateUpCallback;
        }

        @Override
        public boolean handleOnNavigateUp() {
            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                return onNavigateUpCallback.handleOnNavigateUp();
            }
            return false;
        }

        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                synchronized (onNavigateUpCallback) {
                    lifecycle.removeObserver(this);
                    onNavigateUpCallbacks.remove(this);
                }
            }
        }

        public void onRemoved() {
            lifecycle.removeObserver(this);
        }
    }
}
