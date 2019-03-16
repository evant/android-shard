package me.tatarka.shard.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

import java.util.WeakHashMap;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.activity.ActivityCallbacksOwner;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;

/**
 * Utilities to obtain a {@link ShardOwner}.
 */
public final class ShardOwners {

    private ShardOwners() {
    }

    /**
     * Obtains a {@link Shard} from the given {@link Context}. The context must either be a
     * {@link ShardOwner} itself, or implement {@link ViewModelStoreOwner} and {@link LifecycleOwner}.
     *
     * @throws IllegalArgumentException If {@code ShardOwner} cannot be obtained from the context.
     */
    @SuppressLint("WrongConstant")
    public static ShardOwner get(Context context) {
        if (context instanceof ShardOwner) {
            return (ShardOwner) context;
        }
        if (context instanceof ViewModelStoreOwner && context instanceof SavedStateRegistryOwner) {
            return WrappingShardOwner.of(context);
        }
        ShardOwner owner = (ShardOwner) context.getSystemService(ShardOwnerContextWrapper.SHARD_OWNER);
        if (owner != null) {
            return owner;
        }
        throw new IllegalArgumentException("Cannot obtain ShardOwner from context: " + context + ". Make sure your activity is a ComponentActivity or implements ShardOwner");
    }

    static class WrappingShardOwner implements ShardOwner {
        static WrappingShardOwner of(Context context) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                WeakHashMap<Activity, WrappingShardOwner> map = MAP;
                WrappingShardOwner owner = map.get(activity);
                if (owner == null) {
                    owner = new WrappingShardOwner(context);
                    map.put(activity, owner);
                }
                return owner;
            } else {
                return new WrappingShardOwner(context);
            }
        }

        private final Context context;
        final ComponentCallbacksDispatcher callbacks;
        @Nullable
        WrappingActivityCallbacks activityCallbacks;

        private WrappingShardOwner(Context context) {
            this.context = context;
            callbacks = new ComponentCallbacksDispatcher(this);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return ((LifecycleOwner) context).getLifecycle();
        }

        @NonNull
        @Override
        public ViewModelStore getViewModelStore() {
            return ((ViewModelStoreOwner) context).getViewModelStore();
        }

        @NonNull
        @Override
        public SavedStateRegistry getSavedStateRegistry() {
            return ((SavedStateRegistryOwner) context).getSavedStateRegistry();
        }

        @NonNull
        @Override
        public Context getContext() {
            return context;
        }

        @NonNull
        @Override
        public Shard.Factory getShardFactory() {
            return (context instanceof ShardFactoryProvider)
                    ? ((ShardFactoryProvider) context).getShardFactory()
                    : Shard.DefaultFactory.getInstance();
        }

        @NonNull
        @Override
        public ActivityCallbacks getActivityCallbacks() {
            if (context instanceof ActivityCallbacksOwner) {
                return ((ActivityCallbacksOwner) context).getActivityCallbacks();
            } else if (context instanceof ComponentActivity) {
                if (activityCallbacks == null) {
                    activityCallbacks = new WrappingActivityCallbacks((ComponentActivity) context);
                }
                return activityCallbacks;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @NonNull
        @Override
        public ComponentCallbacks getComponentCallbacks() {
            return callbacks;
        }
    }

    static class WrappingActivityCallbacks implements ActivityCallbacks {
        final ComponentActivity activity;
        final ActivityCallbacksDispatcher dispatcher;

        WrappingActivityCallbacks(ComponentActivity activity) {
            this.activity = activity;
            dispatcher = new ActivityCallbacksDispatcher(activity);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addOnActivityResultCallback(int requestCode, @NonNull OnActivityResultCallback onActivityResultCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeActivityResultCallback(int requestCode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void requestPermissions(@NonNull String[] permissions, int requestCode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addOnRequestPermissionResultCallback(int requestCode, @NonNull OnRequestPermissionResultCallback onRequestPermissionResultCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeOnRequestPermissionResultCallback(int requestCode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInMultiWindowMode() {
            return dispatcher.isInMultiWindowMode();
        }

        @Override
        public void addOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback onMultiWindowModeChangedCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback onMultiWindowModeChangedCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInPictureInPictureMode() {
            return dispatcher.isInPictureInPictureMode();
        }

        @Override
        public void addOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback onPictureInPictureModeChangedCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback onPictureInPictureModeChangedCallback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addOnBackPressedCallback(OnBackPressedCallback onBackPressedCallback) {
            dispatcher.addOnBackPressedCallback(onBackPressedCallback);
        }

        @Override
        public void removeOnBackPressedCallback(OnBackPressedCallback onBackPressedCallback) {
            dispatcher.removeOnBackPressedCallback(onBackPressedCallback);
        }
    }

    static final WeakHashMap<Activity, WrappingShardOwner> MAP = new WeakHashMap<>();
}
