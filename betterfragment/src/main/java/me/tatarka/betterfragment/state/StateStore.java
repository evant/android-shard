package me.tatarka.betterfragment.state;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class StateStore implements StateSaver<Bundle> {

    private final ArrayMap<String, StateSaver<?>> stateSavers = new ArrayMap<>();
    private final ArrayMap<String, Parcelable> pendingState = new ArrayMap<>();
    private boolean stateRestored;

    public boolean isStateRestored() {
        return stateRestored;
    }

    public void addStateSaver(@NonNull String key, @NonNull StateSaver saver) {
        stateSavers.put(key, saver);
        if (pendingState.containsKey(key)) {
            Parcelable value = pendingState.remove(key);
            saver.restoreState(value);
        }
    }

    public void removeStateSaver(@NonNull String key) {
        stateSavers.remove(key);
    }

    @Nullable
    @Override
    public Bundle saveState() {
        if (stateSavers.isEmpty()) {
            return null;
        }
        Bundle bundle = new Bundle();
        for (Map.Entry<String, StateSaver<?>> entry : stateSavers.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue().saveState());
        }
        return bundle;
    }

    @Override
    public void restoreState(@Nullable Bundle state) {
        stateRestored = true;
        if (state != null) {
            state.setClassLoader(getClass().getClassLoader());
            for (String key : state.keySet()) {
                Parcelable value = state.getParcelable(key);
                StateSaver stateSaver = stateSavers.get(key);
                if (stateSaver != null) {
                    stateSaver.restoreState(value);
                } else {
                    pendingState.put(key, value);
                }
            }
        }
    }
}
