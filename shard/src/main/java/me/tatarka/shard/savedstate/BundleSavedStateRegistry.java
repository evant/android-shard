package me.tatarka.shard.savedstate;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.app.ShardActivity;

/**
 * Helps with implementing {@link SavedStateRegistry}. Will save and restore state for any added
 * {@link SavedStateProvider}s.
 *
 * @see ShardActivity for an example of how to use.
 */
public final class BundleSavedStateRegistry implements SavedStateRegistry {

    private final ArrayMap<String, SavedStateProvider> stateSavers = new ArrayMap<>();
    private Bundle pendingState;

    @Override
    public boolean isRestored() {
        return pendingState != null;
    }

    @Override
    public <T extends Parcelable> void registerSavedStateProvider(@NonNull String key, @NonNull SavedStateProvider<T> instanceStateSaver) {
        stateSavers.put(key, instanceStateSaver);
        if (pendingState != null) {
            T value = pendingState.getParcelable(key);
            if (value != null) {
                pendingState.remove(key);
                instanceStateSaver.restoreState(value);
            }
        }
    }

    @Override
    public void unregisterSavedStateProvider(@NonNull String key) {
        stateSavers.remove(key);
    }

    /**
     * The owner of this {@link SavedStateRegistry} should call this to perform state saving, it
     * will call all registered providers.
     */
    @NonNull
    public Bundle performSave() {
        int size = stateSavers.size();
        if (size == 0) {
            return Bundle.EMPTY;
        }
        Bundle outState = new Bundle(size);
        for (int i = 0; i < size; i++) {
            String key = stateSavers.keyAt(i);
            SavedStateProvider instanceStateSaver = stateSavers.valueAt(i);
            Parcelable value = instanceStateSaver.saveState();
            if (value != null) {
                outState.putParcelable(key, value);
            }
        }
        return outState;
    }

    /**
     * The owner of this {@link SavedStateRegistry} should call this to restore the saved state.
     */
    @SuppressWarnings("unchecked")
    public void performRestore(@Nullable Bundle instanceState) {
        if (instanceState == null) {
            return;
        }
        pendingState = instanceState;
        for (int i = 0; i < stateSavers.size(); i++) {
            String key = stateSavers.keyAt(i);
            Parcelable childState = instanceState.getParcelable(key);
            if (childState != null) {
                SavedStateProvider instanceStateSaver = stateSavers.valueAt(i);
                instanceStateSaver.restoreState(childState);
            }
        }
    }
}
