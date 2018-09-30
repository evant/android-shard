package me.tatarka.shard.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.state.InstanceStateSaver;
import me.tatarka.shard.state.InstanceStateStore;

final class NestedActivityCallbackDispatcher extends BaseActivityCallbackDispatcher implements InstanceStateSaver<NestedActivityCallbackDispatcher.State>, BaseActivityCallbackDispatcher.NestedCallbackListener {

    private static final String STATE_ACTIVITY_CALLBACK_DISPATCHER = "me.tatarka.shard.ActivityCallbackDispatcher";

    private final BaseActivityCallbackDispatcher parentCallbacks;
    private final InstanceStateStore stateStore;
    private boolean pendingActivityResult;
    private boolean pendingRequestPermission;

    NestedActivityCallbackDispatcher(BaseActivityCallbackDispatcher parentCallbacks, InstanceStateStore stateStore) {
        this.parentCallbacks = parentCallbacks;
        this.stateStore = stateStore;
        parentCallbacks.addNestedCallbackListener(this);
        stateStore.add(STATE_ACTIVITY_CALLBACK_DISPATCHER, this);
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

    @Nullable
    @Override
    public State onSaveInstanceState() {
        return pendingActivityResult || pendingRequestPermission
                ? new State(pendingActivityResult, pendingRequestPermission)
                : null;
    }

    @Override
    public void onRestoreInstanceState(@NonNull State instanceState) {
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

    public void destroy() {
        stateStore.remove(STATE_ACTIVITY_CALLBACK_DISPATCHER);
        parentCallbacks.removeNestedCallbackListener(this);
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
