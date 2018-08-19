package me.tatarka.betterfragment.test;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import me.tatarka.betterfragment.FragmentOwner;

public class TestFragmentOwner implements FragmentOwner {

    final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    final ViewModelStore viewModelStore = new ViewModelStore();
    boolean willRestoreState;

    @Override
    public boolean willRestoreState() {
        return willRestoreState;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
}
