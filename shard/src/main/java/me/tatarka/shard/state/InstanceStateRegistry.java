package me.tatarka.shard.state;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

public final class InstanceStateRegistry implements InstanceStateStore, InstanceStateSaver<Bundle> {

    private final ArrayMap<String, InstanceStateSaver> stateSavers = new ArrayMap<>();
    private Bundle pendingState;

    @Override
    public boolean isStateRestored() {
        return pendingState != null;
    }

    @Override
    public <T extends Parcelable> void add(@NonNull String key, @NonNull InstanceStateSaver<T> instanceStateSaver) {
        stateSavers.put(key, instanceStateSaver);
        if (pendingState != null) {
            T value = pendingState.getParcelable(key);
            if (value != null) {
                pendingState.remove(key);
                instanceStateSaver.onRestoreInstanceState(value);
            }
        }
    }

    @Override
    public void remove(@NonNull String key) {
        stateSavers.remove(key);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        int size = stateSavers.size();
        if (size == 0) {
            return Bundle.EMPTY;
        }
        Bundle outState = new Bundle(size);
        for (int i = 0; i < size; i++) {
            String key = stateSavers.keyAt(i);
            InstanceStateSaver instanceStateSaver = stateSavers.valueAt(i);
            Parcelable value = instanceStateSaver.onSaveInstanceState();
            if (value != null) {
                outState.putParcelable(key, value);
            }
        }
        return outState;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRestoreInstanceState(@NonNull Bundle instanceState) {
        pendingState = instanceState;
        for (int i = 0; i < stateSavers.size(); i++) {
            String key = stateSavers.keyAt(i);
            Parcelable childState = instanceState.getParcelable(key);
            if (childState != null) {
                InstanceStateSaver instanceStateSaver = stateSavers.valueAt(i);
                instanceStateSaver.onRestoreInstanceState(childState);
            }
        }
    }
}
