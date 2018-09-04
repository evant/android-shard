package me.tatarka.betterfragment.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.WeakHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import me.tatarka.betterfragment.state.StateStore;

/**
 * Utilities to obtain a {@link FragmentOwner}.
 */
public final class FragmentOwners {

    private FragmentOwners() {
    }

    public static FragmentOwner get(View view) {
        if (view.isInEditMode()) {
            return new FakeOwner();
        } else {
            return get(view.getContext());
        }
    }

    @SuppressLint("WrongConstant")
    public static FragmentOwner get(Context context) {
        if (context instanceof FragmentOwner) {
            return (FragmentOwner) context;
        }
        if (context instanceof ViewModelStoreOwner && context instanceof LifecycleOwner) {
            return WrappingFragmentOwner.of(context);
        }
        FragmentOwner owner = (FragmentOwner) context.getSystemService(FragmentOwnerContextWrapper.FRAGMENT_OWNER);
        if (owner != null) {
            return owner;
        }
        throw new IllegalArgumentException("Cannot obtain FragmentOwner from context: " + context + ". Make sure your activity is an AppCompatActivity or implements FragmentOwner");
    }

    static class FakeOwner implements FragmentOwner {
        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return null;
        }

        @NonNull
        @Override
        public ViewModelStore getViewModelStore() {
            return null;
        }

        @NonNull
        @Override
        public StateStore getStateStore() {
            return null;
        }

        @Override
        public Context getContext() {
            return null;
        }
    }

    static class WrappingFragmentOwner implements FragmentOwner {
        static WrappingFragmentOwner of(Context context) {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                StateCallbacks stateCallbacks = StateCallbacks.getInstance(context);
                WrappingFragmentOwner owner = stateCallbacks.map.get(activity);
                if (owner == null) {
                    owner = new WrappingFragmentOwner(context);
                    stateCallbacks.map.put(activity, owner);
                }
                return owner;
            } else {
                return new WrappingFragmentOwner(context);
            }
        }

        private final Context context;
        final StateStore stateStore = new StateStore();

        private WrappingFragmentOwner(Context context) {
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
        public StateStore getStateStore() {
            return stateStore;
        }

        @Override
        public Context getContext() {
            return context;
        }
    }

    static class StateCallbacks implements Application.ActivityLifecycleCallbacks {
        private static final String STATE_FRAGMENT = "me.tatarka.betterfragment.app.Fragment";
        private static StateCallbacks INSTANCE;

        static StateCallbacks getInstance(Context context) {
            if (INSTANCE == null) {
                INSTANCE = new StateCallbacks();
                ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(INSTANCE);
            }
            return INSTANCE;
        }

        final WeakHashMap<Activity, WrappingFragmentOwner> map = new WeakHashMap<>();

        @Override
        public void onActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
            WrappingFragmentOwner owner = WrappingFragmentOwner.of(activity);
            if (savedInstanceState != null) {
                Bundle state = savedInstanceState.getBundle(STATE_FRAGMENT);
                if (state != null) {
                    owner.stateStore.onRestoreState(state);
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
            WrappingFragmentOwner owner = WrappingFragmentOwner.of(activity);
            Bundle out = new Bundle();
            owner.stateStore.onSaveState(out);
            outState.putBundle(STATE_FRAGMENT, out);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
