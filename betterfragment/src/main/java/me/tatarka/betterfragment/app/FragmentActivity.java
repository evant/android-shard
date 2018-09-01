package me.tatarka.betterfragment.app;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;

public class FragmentActivity extends Activity implements FragmentOwner {

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final ActivityCallbackRegistry activityCallbacks = new ActivityCallbackRegistry();
    private ViewModelStore viewModelStore;
    private boolean isRetaining;
    private boolean willRestoreState;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        willRestoreState = savedInstanceState != null;
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

    @Override
    @CallSuper
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        activityCallbacks.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @CallSuper
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        activityCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    @CallSuper
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        activityCallbacks.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Override
    @CallSuper
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        activityCallbacks.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    @Override
    @CallSuper
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        activityCallbacks.onConfigurationChanged(newConfig);
    }

    @CallSuper
    public void onLowMemory() {
        activityCallbacks.onLowMemory();
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

    @Override
    public boolean willRestoreState() {
        return willRestoreState;
    }

    @NonNull
    @Override
    public ActivityCallbacks getActivityCallbacks() {
        return activityCallbacks;
    }
}
