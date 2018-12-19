package me.tatarka.shard.savedstate;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provides callbacks for saving and restoring state.
 */
public interface SavedStateProvider<S extends Parcelable> {

    /**
     * Called when state should be saved. This will happen after {@code onStop()} and before
     * {@code onDestroy()}. If null is returned, {@link #restoreState(Parcelable)}
     * will not be called.
     */
    @Nullable
    S saveState();

    /**
     * Called when state should be restored.
     */
    void restoreState(@NonNull S state);
}
