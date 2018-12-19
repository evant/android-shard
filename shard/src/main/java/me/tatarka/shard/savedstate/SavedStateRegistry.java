package me.tatarka.shard.savedstate;

import android.os.Parcelable;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public interface SavedStateRegistry {

    /**
     * Returns true if state has been restored, false otherwise.
     */
    @MainThread
    boolean isRestored();

    /**
     * Registers a {@link SavedStateProvider}, this will call {@link SavedStateProvider#restoreState(Parcelable)}
     * immediately if there is state to restore.
     *
     * @param key a unique key for this state saver.
     */
    @MainThread
    <T extends Parcelable> void registerSavedStateProvider(@NonNull String key, @NonNull SavedStateProvider<T> instanceStateSaver);

    /**
     * Unregisters a {@link SavedStateProvider}. It's callbacks will not be called after this.
     *
     * @param key a unique key for this state saver.
     */
    @MainThread
    void unregisterSavedStateProvider(@NonNull String key);
}
