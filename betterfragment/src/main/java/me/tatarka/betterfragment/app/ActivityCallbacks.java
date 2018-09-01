package me.tatarka.betterfragment.app;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public abstract class ActivityCallbacks {

    @MainThread
    public abstract void addObserver(@NonNull ActivityCallbacksObserver observer);

    @MainThread
    public abstract void removeObserver(@NonNull ActivityCallbacksObserver observer);
}
