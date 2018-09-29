package me.tatarka.betterfragment.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import me.tatarka.betterfragment.state.InstanceStateRegistry;

public class FragmentActivity extends Activity implements FragmentOwner {

    private static final String STATE_FRAGMENT = "me.tatarka.betterfragment.app.Fragment";

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final InstanceStateRegistry stateStore = new InstanceStateRegistry();
    private ViewModelStore viewModelStore;
    private boolean isRetaining;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle state = savedInstanceState.getBundle(STATE_FRAGMENT);
            if (state != null) {
                stateStore.onRestoreInstanceState(state);
            }
        }
        viewModelStore = (ViewModelStore) getLastNonConfigurationInstance();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    @CallSuper
    protected void onStop() {
        super.onStop();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        super.onSaveInstanceState(outState);
        outState.putBundle(STATE_FRAGMENT, stateStore.onSaveInstanceState());
    }

    @Override
    public final Object onRetainNonConfigurationInstance() {
        isRetaining = true;
        return viewModelStore;
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();
        if (viewModelStore != null && !isRetaining) {
            viewModelStore.clear();
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
        }
        return viewModelStore;
    }

    @NonNull
    @Override
    public InstanceStateRegistry getInstanceStateStore() {
        return stateStore;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
