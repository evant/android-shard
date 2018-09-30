package me.tatarka.shard.state;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provides callbacks for saving and restoring state.
 */
public interface InstanceStateSaver<T extends Parcelable> {

    /**
     * Called when state should be saved. This will happen after {@code onStop()} and before
     * {@code onDestroy()}. If null is returned, {@link #onRestoreInstanceState(Parcelable)}
     * will not be called.
     */
    @Nullable
    T onSaveInstanceState();

    /**
     * Called when state should be restored.
     */
    void onRestoreInstanceState(@NonNull T instanceState);
}
