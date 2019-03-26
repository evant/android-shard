package me.tatarka.shard.fragment.app;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentHostCallback;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.BaseActivityCallbacksDispatcher;
import me.tatarka.shard.app.NestedActivityCallbacksDispatcher;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.content.ComponentCallbacks;

/**
 * A {@link me.tatarka.shard.app.Shard} that can hosts fragments.
 */
public abstract class FragmentShard extends Shard {

    private static final String FRAGMENT_STATE_KEY = "me.tatarka.shard.FragmentShard";
    private static final String FRAGMENTS_TAG = "android:support:fragments";

    private HostCallback hostCallback;
    private FragmentController fragmentController;

    private final Map<Fragment, NestedActivityCallbacksDispatcher> fragmentDispatchers = new ArrayMap<>();
    private boolean created;

    public FragmentShard() {
        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                switch (event) {
                    case ON_CREATE:
                        hostCallback = new HostCallback();
                        fragmentController = FragmentController.createController(hostCallback);

                        fragmentController.getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                            @Override
                            public void onFragmentPreAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
                                NestedActivityCallbacksDispatcher dispatcher = new NestedActivityCallbacksDispatcher((BaseActivityCallbacksDispatcher) getActivityCallbacks(), f);
                                dispatcher.addOnMultiWindowModeChangedCallback(hostCallback);
                                dispatcher.addOnPictureInPictureModeChangedCallback(hostCallback);
                                fragmentDispatchers.put(f, dispatcher);
                            }

                            @Override
                            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                                fragmentDispatchers.remove(f);
                            }
                        }, false);


                        fragmentController.attachHost(null);
                        Bundle savedState = getSavedStateRegistry().consumeRestoredStateForKey(FRAGMENT_STATE_KEY);
                        if (savedState != null) {
                            Parcelable fragmentState = savedState.getParcelable(FRAGMENTS_TAG);
                            fragmentController.restoreSaveState(fragmentState);
                        }
                        getSavedStateRegistry().registerSavedStateProvider(FRAGMENT_STATE_KEY, new SavedStateRegistry.SavedStateProvider() {
                            @NonNull
                            @Override
                            public Bundle saveState() {
                                Parcelable state = fragmentController.saveAllState();
                                if (state != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putBundle(FRAGMENTS_TAG, bundle);
                                    return bundle;
                                } else {
                                    return Bundle.EMPTY;
                                }
                            }
                        });

                        fragmentController.dispatchCreate();

                        getComponentCallbacks().addOnConfigurationChangedListener(hostCallback);
                        getComponentCallbacks().addOnTrimMemoryListener(hostCallback);

                        break;
                    case ON_START:
                        if (!created) {
                            created = true;
                            fragmentController.dispatchActivityCreated();
                        }
                        fragmentController.noteStateNotSaved();
                        fragmentController.execPendingActions();
                        fragmentController.dispatchStart();
                        break;
                    case ON_RESUME:
                        fragmentController.noteStateNotSaved();
                        fragmentController.execPendingActions();
                        fragmentController.dispatchResume();
                        break;
                    case ON_PAUSE:
                        fragmentController.dispatchPause();
                        break;
                    case ON_STOP:
                        fragmentController.dispatchStop();
                        break;
                    case ON_DESTROY:
                        fragmentController.dispatchDestroy();
                        break;
                }
            }
        });
    }

    public final FragmentManager getFragmentManager() {
        return fragmentController.getSupportFragmentManager();
    }

    class HostCallback extends FragmentHostCallback<FragmentShard>
            implements ViewModelStoreOwner,
            ComponentCallbacks.OnConfigurationChangedListener,
            ComponentCallbacks.OnTrimMemoryListener,
            ActivityCallbacks.OnMultiWindowModeChangedCallback,
            ActivityCallbacks.OnPictureInPictureModeChangedCallback {

        HostCallback() {
            super(FragmentShard.this.getActivity(), new Handler(), 0);
        }

        @NonNull
        @Override
        public ViewModelStore getViewModelStore() {
            return FragmentShard.this.getViewModelStore();
        }

        @Override
        public void onDump(@NonNull String prefix, @Nullable FileDescriptor fd, @NonNull PrintWriter writer, @Nullable String[] args) {
            //TODO
        }

        @Override
        public boolean onShouldSaveFragmentState(@NonNull Fragment fragment) {
            return super.onShouldSaveFragmentState(fragment);
        }

        @Nullable
        @Override
        public FragmentShard onGetHost() {
            return FragmentShard.this;
        }

        @Override
        public void onSupportInvalidateOptionsMenu() {
            FragmentShard.this.getActivity().invalidateOptionsMenu();
        }

        @Override
        public void onStartActivityFromFragment(@NonNull Fragment fragment, Intent intent, int requestCode) {
            onStartActivityFromFragment(fragment, intent, requestCode, null);
        }

        @Override
        public void onStartActivityFromFragment(@NonNull Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
            fragmentDispatchers.get(fragment).startActivityForResult(intent, requestCode, options);
        }

        @Override
        public void onStartIntentSenderFromFragment(@NonNull Fragment fragment, IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {
            //TODO
        }

        @Override
        public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] permissions, int requestCode) {
            fragmentDispatchers.get(fragment).requestPermissions(permissions, requestCode);
        }

        @Override
        public boolean onShouldShowRequestPermissionRationale(@NonNull String permission) {
            return getActivityCallbacks().shouldShowRequestPermissionRationale(permission);
        }

        @Override
        public boolean onHasWindowAnimations() {
            return FragmentShard.this.getActivity().getWindow() != null;
        }

        @Override
        public int onGetWindowAnimations() {
            Window window = FragmentShard.this.getActivity().getWindow();
            return window != null ? window.getAttributes().windowAnimations : 0;
        }

        @Nullable
        @Override
        public View onFindViewById(int id) {
            return FragmentShard.this.findViewById(id);
        }

        @Override
        public boolean onHasView() {
            return getView() != null;
        }

        @Override
        public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
            fragmentController.dispatchMultiWindowModeChanged(isInMultiWindowMode);
        }

        @Override
        public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
            fragmentController.dispatchPictureInPictureModeChanged(isInPictureInPictureMode);
        }

        @Override
        public void onConfigurationChanged(@NonNull Configuration newConfig) {
            fragmentController.noteStateNotSaved();
            fragmentController.dispatchConfigurationChanged(newConfig);
        }

        @Override
        public void onTrimMemory(int level) {
            if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
                fragmentController.dispatchLowMemory();
            }
        }
    }

    private FragmentActivity getActivity() {
        return (FragmentActivity) ((ContextWrapper) getContext()).getBaseContext();
    }
}
