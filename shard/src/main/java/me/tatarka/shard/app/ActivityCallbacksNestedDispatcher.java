package me.tatarka.shard.app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

import me.tatarka.shard.activity.ActivityCallbacks;

/**
 * Helps dispatch {@link ActivityCallbacks} to nested components.
 */
public final class ActivityCallbacksNestedDispatcher extends ActivityCallbacksDispatcher implements
        SavedStateRegistry.SavedStateProvider,
        LifecycleEventObserver,
        ActivityCallbacks.OnActivityCallbacks {

    private static final String STATE_ACTIVITY_CALLBACK_DISPATCHER = "me.tatarka.shard.ActivityCallbacksActivityDispatcher";
    private static final String KEY = "key";

    private final ActivityCallbacks parentCallbacks;
    private boolean pendingActivityResult;
    private boolean pendingRequestPermission;

    public <O extends SavedStateRegistryOwner & LifecycleOwner> ActivityCallbacksNestedDispatcher(ActivityCallbacks parentCallbacks, O owner) {
        super(owner);
        this.parentCallbacks = parentCallbacks;
        parentCallbacks.addOnActivityCallbacks(this);
        SavedStateRegistry registry = owner.getSavedStateRegistry();
        restoreState(registry);
        registry.registerSavedStateProvider(STATE_ACTIVITY_CALLBACK_DISPATCHER, this);
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
    public void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        pendingActivityResult = true;
        parentCallbacks.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public void startIntentSenderForResult(@NonNull IntentSender intent, int requestCode,
                                           @Nullable Intent fillIntent, int flagsMask, int flagsValues, int extraFlags,
                                           Bundle options) throws IntentSender.SendIntentException {
        pendingActivityResult = true;
        parentCallbacks.startIntentSenderForResult(intent, requestCode, fillIntent, flagsMask, flagsValues, extraFlags, options);

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

    @NonNull
    @Override
    public Bundle saveState() {
        if (pendingActivityResult || pendingRequestPermission) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY, new State(pendingActivityResult, pendingRequestPermission));
            return bundle;
        } else {
            return Bundle.EMPTY;
        }
    }

    private void restoreState(SavedStateRegistry registry) {
        Bundle stateBundle = registry.consumeRestoredStateForKey(STATE_ACTIVITY_CALLBACK_DISPATCHER);
        if (stateBundle == null) {
            return;
        }
        State state = stateBundle.getParcelable(KEY);
        if (state == null) {
            return;
        }
        pendingActivityResult = state.pendingActivityResult;
        pendingRequestPermission = state.pendingRequestPermission;
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

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            parentCallbacks.removeOnActivityCallbacks(this);
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
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
