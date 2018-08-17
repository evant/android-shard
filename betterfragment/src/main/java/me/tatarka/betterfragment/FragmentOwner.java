package me.tatarka.betterfragment;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

public interface FragmentOwner extends LifecycleOwner, ViewModelStoreOwner {
    /**
     * Returns true if fragment state will be restored, common impl would
     * be {@code savedInstanceState != null}.
     */
    boolean willRestoreState();
}
