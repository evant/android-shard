package me.tatarka.shard.app;

import android.content.Intent;
import android.util.SparseArray;

import java.util.ArrayList;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.activity.OnNavigateUpCallback;

abstract class BaseActivityCallbacksDispatcher implements ActivityCallbacks {

    protected final LifecycleOwner lifecycleOwner;
    private final ArrayList<NestedCallbackListener> nestedCallbackListeners = new ArrayList<>();
    private final SparseArray<OnActivityResultCallback> activityResultListeners = new SparseArray<>();
    private final SparseArray<OnRequestPermissionResultCallback> requestPermissionResultListeners = new SparseArray<>();
    private final ArrayList<OnMultiWindowModeChangedCallback> multiWindowModeChangedListeners = new ArrayList<>();
    private final ArrayList<OnPictureInPictureModeChangedCallback> pictureInPictureModeChangedListeners = new ArrayList<>();

    protected BaseActivityCallbacksDispatcher(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void addOnActivityResultCallback(int requestCode, @NonNull OnActivityResultCallback listener) {
        activityResultListeners.put(requestCode, listener);
    }

    @Override
    public void removeActivityResultCallback(int requestCode) {
        activityResultListeners.remove(requestCode);
    }

    @Override
    public void addOnRequestPermissionResultCallback(int requestCode, @NonNull OnRequestPermissionResultCallback listener) {
        requestPermissionResultListeners.put(requestCode, listener);
    }

    @Override
    public void removeOnRequestPermissionResultCallback(int requestCode) {
        requestPermissionResultListeners.remove(requestCode);
    }

    protected void addNestedCallbackListener(@NonNull NestedCallbackListener listener) {
        nestedCallbackListeners.add(listener);
    }

    protected void removeNestedCallbackListener(@NonNull NestedCallbackListener listener) {
        nestedCallbackListeners.remove(listener);
    }

    @Override
    public final void addOnBackPressedCallback(OnBackPressedCallback callback) {
        addOnBackPressedCallback(lifecycleOwner, callback);
    }

    protected abstract void addOnBackPressedCallback(LifecycleOwner owner, OnBackPressedCallback callback);

    @Override
    public final void addOnNavigateUpCallback(OnNavigateUpCallback onNavigateUpCallback) {
        addOnNavigateUpCallback(lifecycleOwner, onNavigateUpCallback);
    }

    protected abstract void addOnNavigateUpCallback(LifecycleOwner owner, OnNavigateUpCallback onNavigateUpCallback);

    @Override
    public void addOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback listener) {
        multiWindowModeChangedListeners.add(listener);
    }

    @Override
    public void removeOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback listener) {
        multiWindowModeChangedListeners.remove(listener);
    }

    @Override
    public void addOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback listener) {
        pictureInPictureModeChangedListeners.add(listener);
    }

    @Override
    public void removeOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback listener) {
        pictureInPictureModeChangedListeners.remove(listener);
    }

    public final void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            if (listener.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        OnActivityResultCallback listener = activityResultListeners.get(requestCode);
        if (listener != null) {
            listener.onActivityResult(resultCode, data);
        }
    }

    public final void dispatchOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            if (listener.onRequestPermissionResult(requestCode, permissions, grantResults)) {
                return;
            }
        }

        OnRequestPermissionResultCallback listener = requestPermissionResultListeners.get(requestCode);
        if (listener != null) {
            listener.onRequestPermissionResult(permissions, grantResults);
        }
    }

    public final void dispatchOnMultiWindowModeChanged(boolean isInMultiWindowMode) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            listener.onMultiWindowModeChanged(isInMultiWindowMode);
        }
        for (int i = 0, size = multiWindowModeChangedListeners.size(); i < size; i++) {
            OnMultiWindowModeChangedCallback listener = multiWindowModeChangedListeners.get(i);
            listener.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    public final void dispatchOnPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            listener.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
        for (int i = 0, size = pictureInPictureModeChangedListeners.size(); i < size; i++) {
            OnPictureInPictureModeChangedCallback listener = pictureInPictureModeChangedListeners.get(i);
            listener.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
    }

    interface NestedCallbackListener {
        boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

        void onMultiWindowModeChanged(boolean isInMultiWindowMode);

        void onPictureInPictureModeChanged(boolean isInPictureInPictureMode);
    }
}
