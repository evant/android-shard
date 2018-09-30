package me.tatarka.shard.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.WeakHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.state.InstanceStateStore;

/**
 * Utilities to obtain a {@link ShardOwner}.
 */
public final class ShardOwners {

    private ShardOwners() {
    }

    @SuppressLint("WrongConstant")
    public static ShardOwner get(Context context) {
        if (context instanceof ShardOwner) {
            return (ShardOwner) context;
        }
        if (context instanceof ViewModelStoreOwner && context instanceof LifecycleOwner) {
            return WrappingShardOwner.of(context);
        }
        ShardOwner owner = (ShardOwner) context.getSystemService(ShardOwnerContextWrapper.SHARD_OWNER);
        if (owner != null) {
            return owner;
        }
        throw new IllegalArgumentException("Cannot obtain ShardOwner from context: " + context + ". Make sure your activity is an AppCompatActivity or implements ShardOwner");
    }

    static class WrappingShardOwner implements ShardOwner {
        static WrappingShardOwner of(Context context) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                StateCallbacks stateCallbacks = StateCallbacks.getInstance(context);
                WrappingShardOwner owner = stateCallbacks.map.get(activity);
                if (owner == null) {
                    owner = new WrappingShardOwner(context);
                    stateCallbacks.map.put(activity, owner);
                }
                return owner;
            } else {
                return new WrappingShardOwner(context);
            }
        }

        @Nullable
        static WrappingShardOwner find(Activity activity) {
            StateCallbacks stateCallbacks = StateCallbacks.getInstance(activity);
            return stateCallbacks.map.get(activity);
        }

        private final Context context;
        final InstanceStateRegistry stateStore = new InstanceStateRegistry();
        final ComponentCallbacksDispatcher callbacks = new ComponentCallbacksDispatcher();

        private WrappingShardOwner(Context context) {
            this.context = context;
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
        public InstanceStateStore getInstanceStateStore() {
            return stateStore;
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

    static class StateCallbacks implements Application.ActivityLifecycleCallbacks {
        private static final String STATE_SHARD = "me.tatarka.shard.app.Shard";
        private static StateCallbacks INSTANCE;

        static StateCallbacks getInstance(Context context) {
            if (INSTANCE == null) {
                INSTANCE = new StateCallbacks();
                ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(INSTANCE);
            }
            return INSTANCE;
        }

        final WeakHashMap<Activity, WrappingShardOwner> map = new WeakHashMap<>();

        @Override
        public void onActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
            WrappingShardOwner owner = WrappingShardOwner.of(activity);
            activity.registerComponentCallbacks(owner.callbacks);
            if (savedInstanceState != null) {
                Bundle state = savedInstanceState.getBundle(STATE_SHARD);
                if (state != null) {
                    owner.stateStore.onRestoreInstanceState(state);
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            WrappingShardOwner owner = WrappingShardOwner.of(activity);
            Bundle state = owner.stateStore.onSaveInstanceState();
            outState.putBundle(STATE_SHARD, state);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            WrappingShardOwner owner = WrappingShardOwner.find(activity);
            if (owner != null) {
                activity.unregisterComponentCallbacks(owner.callbacks);
            }
        }
    }
}
