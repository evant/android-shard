package me.tatarka.shard.app;

import android.content.Intent;
import android.util.SparseArray;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

abstract class BaseActivityCallbackDispatcher implements ActivityCallbacks {

    private final ArrayList<NestedCallbackListener> nestedCallbackListeners = new ArrayList<>();
    private final SparseArray<OnActivityResultListener> activityResultListeners = new SparseArray<>();
    private final SparseArray<OnRequestPermissionResultListener> requestPermissionResultListeners = new SparseArray<>();

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

    public void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
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

    public void dispatchOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    interface NestedCallbackListener {
        boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}
