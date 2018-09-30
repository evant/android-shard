package me.tatarka.shard.state;

import android.os.Parcelable;

import androidx.annotation.NonNull;

public interface InstanceStateStore {

    /**
     * Returns true if state has been restored, false otherwise.
     */
    boolean isStateRestored();

    /**
     * Registers a {@link InstanceStateStore}, this will call {@link InstanceStateSaver#onRestoreInstanceState(Parcelable)}
     * immediately if there is state to restore.
     *
     * @param key a unique key for this state saver.
     */
    <T extends Parcelable> void add(@NonNull String key, @NonNull InstanceStateSaver<T> instanceStateSaver);

    /**
     * Unregisters a {@link InstanceStateStore}. It's callbacks will not be called after this.
     *
     * @param key a unique key for this state saver.
     */
    void remove(@NonNull String key);
}
