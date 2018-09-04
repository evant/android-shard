package me.tatarka.betterfragment.app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import me.tatarka.betterfragment.state.StateStoreOwner;

/**
 * Interface that a class that hosts fragments must implement. The owner is a {@link LifecycleOwner}
 * and {@link ViewModelStoreOwner}. See {@link FragmentActivity} for a simple implementation.
 */
public interface FragmentOwner extends LifecycleOwner, ViewModelStoreOwner, StateStoreOwner {
    @NonNull
    Context getContext();
}
