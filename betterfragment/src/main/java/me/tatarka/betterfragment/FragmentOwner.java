package me.tatarka.betterfragment;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * Interface that a class that hosts fragments must implement. The owner is a {@link LifecycleOwner}
 * and {@link ViewModelStoreOwner}. See {@link FragmentActivity} for a simple implementation.
 */
public interface FragmentOwner extends LifecycleOwner, ViewModelStoreOwner {
    /**
     * Returns true if fragment state will be restored, common impl would
     * be {@code savedInstanceState != null}.
     */
    boolean willRestoreState();
}
