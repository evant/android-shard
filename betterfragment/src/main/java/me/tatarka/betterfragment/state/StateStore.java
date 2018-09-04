package me.tatarka.betterfragment.state;

import android.os.Bundle;
import android.util.ArrayMap;

import java.util.Map;

import androidx.annotation.NonNull;

public final class StateStore implements StateSaver {

    private final ArrayMap<String, StateSaver> stateSavers = new ArrayMap<>();
    private Bundle pendingState;

    public boolean isStateRestored() {
        return pendingState != null;
    }

    public void addStateSaver(@NonNull String key, @NonNull StateSaver stateSaver) {
        stateSavers.put(key, stateSaver);
        if (pendingState != null) {
            Bundle value = pendingState.getBundle(key);
            if (value != null) {
                pendingState.remove(key);
                stateSaver.onRestoreState(value);
            }
        }
    }

    public void removeStateSaver(@NonNull String key) {
        stateSavers.remove(key);
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        for (Map.Entry<String, StateSaver> entry : stateSavers.entrySet()) {
            Bundle out = new Bundle();
            entry.getValue().onSaveState(out);
            outState.putBundle(entry.getKey(), out);
        }
    }

    @Override
    public void onRestoreState(@NonNull Bundle instanceState) {
        pendingState = instanceState;
        for (Map.Entry<String, StateSaver> entry : stateSavers.entrySet()) {
            Bundle state = instanceState.getBundle(entry.getKey());
            if (state != null) {
                entry.getValue().onRestoreState(state);
            }
        }
    }
}
