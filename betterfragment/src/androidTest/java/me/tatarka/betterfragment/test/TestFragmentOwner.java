package me.tatarka.betterfragment.test;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.test.InstrumentationRegistry;
import me.tatarka.betterfragment.app.FragmentOwner;
import me.tatarka.betterfragment.state.StateStore;

public class TestFragmentOwner implements FragmentOwner {

    final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    final ViewModelStore viewModelStore = new ViewModelStore();
    final StateStore stateStore = new StateStore();

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

    @Override
    public Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    @NonNull
    @Override
    public StateStore getStateStore() {
        return stateStore;
    }
}
