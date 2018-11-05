package me.tatarka.shard.app;

import android.content.Intent;
import android.util.SparseArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

abstract class BaseActivityCallbacksDispatcher implements ActivityCallbacks {

    private final ArrayList<NestedCallbackListener> nestedCallbackListeners = new ArrayList<>();
    private final SparseArray<OnActivityResultListener> activityResultListeners = new SparseArray<>();
    private final SparseArray<OnRequestPermissionResultListener> requestPermissionResultListeners = new SparseArray<>();
    private final ArrayList<OnMultiWindowModeChangedListener> multiWindowModeChangedListeners = new ArrayList<>();
    private final ArrayList<OnPictureInPictureModeChangedListener> pictureInPictureModeChangedListeners = new ArrayList<>();

    @Override
    public void addOnActivityResultListener(int requestCode, @NonNull OnActivityResultListener listener) {
        activityResultListeners.put(requestCode, listener);
    }

    @Override
    public void removeActivityResultListener(int requestCode) {
        activityResultListeners.remove(requestCode);
    }

    @Override
    public void addOnRequestPermissionResultListener(int requestCode, @NonNull OnRequestPermissionResultListener listener) {
        requestPermissionResultListeners.put(requestCode, listener);
    }

    @Override
    public void removeOnRequestPermissionResultListener(int requestCode) {
        requestPermissionResultListeners.remove(requestCode);
    }

    public void addNestedCallbackListener(@NonNull NestedCallbackListener listener) {
        nestedCallbackListeners.add(listener);
    }

    public void removeNestedCallbackListener(@NonNull NestedCallbackListener listener) {
        nestedCallbackListeners.remove(listener);
    }

    @Override
    public void addOnMultiWindowModeChangedListener(@NonNull OnMultiWindowModeChangedListener listener) {
        multiWindowModeChangedListeners.add(listener);
    }

    @Override
    public void removeOnMultiWindowModeChangedListener(@NonNull OnMultiWindowModeChangedListener listener) {
        multiWindowModeChangedListeners.remove(listener);
    }

    @Override
    public void addOnPictureInPictureModeChangedListener(@NonNull OnPictureInPictureModeChangedListener listener) {
        pictureInPictureModeChangedListeners.add(listener);
    }

    @Override
    public void removeOnPictureInPictureModeChangedListener(@NonNull OnPictureInPictureModeChangedListener listener) {
        pictureInPictureModeChangedListeners.remove(listener);
    }

    public final void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            if (listener.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        OnActivityResultListener listener = activityResultListeners.get(requestCode);
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

        OnRequestPermissionResultListener listener = requestPermissionResultListeners.get(requestCode);
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
            OnMultiWindowModeChangedListener listener = multiWindowModeChangedListeners.get(i);
            listener.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    public final void dispatchOnPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        for (int i = 0, size = nestedCallbackListeners.size(); i < size; i++) {
            NestedCallbackListener listener = nestedCallbackListeners.get(i);
            listener.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
        for (int i = 0, size = pictureInPictureModeChangedListeners.size(); i < size; i++) {
            OnPictureInPictureModeChangedListener listener = pictureInPictureModeChangedListeners.get(i);
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
