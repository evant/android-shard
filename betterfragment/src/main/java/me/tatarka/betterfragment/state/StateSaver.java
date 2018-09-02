package me.tatarka.betterfragment.state;

import android.os.Parcelable;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

public interface StateSaver<S extends Parcelable> {
    @Nullable
    @MainThread
    S saveState();

    @MainThread
    void restoreState(@Nullable S state);
}
