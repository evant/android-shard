package me.tatarka.shard.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import me.tatarka.shard.savedstate.SavedStateProvider;

final class NestedActivityCallbacksDispatcher extends BaseActivityCallbacksDispatcher implements
        SavedStateProvider<NestedActivityCallbacksDispatcher.State>,
        LifecycleObserver,
        BaseActivityCallbacksDispatcher.NestedCallbackListener {

    private static final String STATE_ACTIVITY_CALLBACK_DISPATCHER = "me.tatarka.shard.ActivityCallbacksDispatcher";

    private final BaseActivityCallbacksDispatcher parentCallbacks;
    private boolean pendingActivityResult;
    private boolean pendingRequestPermission;

    NestedActivityCallbacksDispatcher(BaseActivityCallbacksDispatcher parentCallbacks, ShardOwner owner) {
        super(owner);
        this.parentCallbacks = parentCallbacks;
        parentCallbacks.addNestedCallbackListener(this);
        owner.getSavedStateRegistry().registerSavedStateProvider(STATE_ACTIVITY_CALLBACK_DISPATCHER, this);
        owner.getLifecycle().addObserver(this);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode) {
        pendingActivityResult = true;
        parentCallbacks.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
        pendingActivityResult = true;
        parentCallbacks.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void requestPermissions(@NonNull String[] permissions, int requestCode) {
        pendingRequestPermission = true;
        parentCallbacks.requestPermissions(permissions, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return parentCallbacks.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public boolean isInMultiWindowMode() {
        return parentCallbacks.isInMultiWindowMode();
    }

    @Override
    public boolean isInPictureInPictureMode() {
        return parentCallbacks.isInPictureInPictureMode();
    }

    @Override
    public void addOnBackPressedCallback(LifecycleOwner owner, OnBackPressedCallback callback) {
        parentCallbacks.addOnBackPressedCallback(owner, callback);
    }

    @Override
    public void removeOnBackPressedCallback(OnBackPressedCallback callback) {
        parentCallbacks.removeOnBackPressedCallback(callback);
    }

    @Nullable
    @Override
    public State saveState() {
        return pendingActivityResult || pendingRequestPermission
                ? new State(pendingActivityResult, pendingRequestPermission)
                : null;
    }

    @Override
    public void restoreState(@NonNull State instanceState) {
        pendingActivityResult = instanceState.pendingActivityResult;
        pendingRequestPermission = instanceState.pendingRequestPermission;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (pendingActivityResult) {
            pendingActivityResult = false;
            dispatchOnActivityResult(requestCode, resultCode, data);
            return true;
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (pendingRequestPermission) {
            pendingRequestPermission = false;
            dispatchOnRequestPermissionsResult(requestCode, permissions, grantResults);
            return true;
        }
        return false;
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        dispatchOnMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        dispatchOnMultiWindowModeChanged(isInPictureInPictureMode);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        parentCallbacks.removeNestedCallbackListener(this);
        lifecycleOwner.getLifecycle().removeObserver(this);
    }

    public static class State implements Parcelable {
        final boolean pendingActivityResult;
        final boolean pendingRequestPermission;

        State(boolean pendingActivityResult, boolean pendingRequestPermission) {
            this.pendingActivityResult = pendingActivityResult;
            this.pendingRequestPermission = pendingRequestPermission;
        }

        State(Parcel in) {
            pendingActivityResult = in.readByte() != 0;
            pendingRequestPermission = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (pendingActivityResult ? 1 : 0));
            dest.writeByte((byte) (pendingRequestPermission ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
}
