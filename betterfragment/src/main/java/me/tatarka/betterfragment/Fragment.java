package me.tatarka.betterfragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

public class Fragment implements FragmentOwner {

    public static int DESTROY_SAVE_STATE = 1;
    public static int DESTROY_FINAL = 2;

    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T newInstance(State state) {
        return (T) DefaultRestoreStateFactory.of(state.fragmentClass).create();
    }

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final Observer observer = new Observer();
    private ViewGroup frame;
    private int id;
    private FragmentOwner owner;
    private Context context;
    private Bundle args;
    private boolean destroyed;
    private boolean willRestoreState;

    public void create(FragmentOwner owner, ViewGroup container, int id) {
        create(owner, container, id, null);
    }

    public void create(FragmentOwner owner, ViewGroup container, State state) {
        create(owner, container, state.id, state);
    }

    void create(FragmentOwner owner, ViewGroup container, int id, @Nullable State state) {
        if (state != null && !state.isFor(this)) {
            throw new IllegalArgumentException("wrong state for fragment: " + toString());
        }
        this.owner = owner;
        this.id = id;
        if (state != null && state.args != null) {
            Bundle args = new Bundle(state.args);
            if (this.args != null) {
                args.putAll(this.args);
            }
            this.args = args;
        }
        context = new FragmentContextWrapper(container.getContext(), this);
        frame = new FrameLayout(getContext());
        frame.setSaveFromParentEnabled(false);
        container.addView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        willRestoreState = state != null;
        onCreate(state != null ? state.savedState : null);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        if (state != null && state.viewState != null) {
            getView().restoreHierarchyState(state.viewState);
        }
        owner.getLifecycle().addObserver(observer);
    }

    public Fragment.State saveState() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        SparseArray<Parcelable> viewState = new SparseArray<>();
        if (frame != null) {
            frame.saveHierarchyState(viewState);
        }
        State state = new State(getClass(), id, args, new Bundle(), viewState);
        onSaveInstanceState(state.savedState);
        return state;
    }

    public void destroy() {
        if (destroyed) {
            throw new IllegalStateException("Fragment is already destroyed");
        }
        destroyed = true;
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        removeViewModelStore(id);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        owner.getLifecycle().removeObserver(observer);
        ((ViewGroup) frame.getParent()).removeView(frame);
    }


    @NonNull
    public Bundle getArgs() {
        if (args == null) {
            args = new Bundle();
        }
        return args;
    }

    @CallSuper
    public void setArgs(@Nullable Bundle args) {
        this.args = args;
    }

    public int getId() {
        return id;
    }

    public ViewGroup getView() {
        return frame;
    }

    public void setContentView(View view) {
        getView().removeAllViews();
        getView().addView(view);
    }

    public <T extends View> T findViewForId(@IdRes int id) {
        return getView().findViewById(id);
    }

    public void setContentView(@LayoutRes int layoutId) {
        LayoutInflater.from(getContext()).inflate(layoutId, getView(), true);
    }

    @CallSuper
    public void onCreate(@Nullable Bundle savedState) {
    }

    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
    }

    public Context getContext() {
        return context;
    }

    private ViewModelStore getViewModelStore(int id) {
        return FragmentManagerViewModel.get(owner.getViewModelStore()).get(id);
    }

    private void removeViewModelStore(int id) {
        FragmentManagerViewModel.get(owner.getViewModelStore()).remove(id);
    }

    public ViewModelProvider getViewModelProvider() {
        return ViewModelProviders.of(this);
    }

    public ViewModelProvider getViewModelProvider(ViewModelProvider.Factory factory) {
        return ViewModelProviders.of(this, factory);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (getContext() == null) {
            throw new IllegalStateException("Your fragment is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        return getViewModelStore(id);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public boolean willRestoreState() {
        return willRestoreState;
    }

    class Observer implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        void onStart() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        void onStop() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            owner.getLifecycle().removeObserver(this);
        }
    }

    public static class State implements Parcelable {
        final Class<? extends Fragment> fragmentClass;
        final int id;
        final Bundle args;
        final Bundle savedState;
        final SparseArray viewState;

        public int getId() {
            return id;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public boolean isFor(Fragment fragment) {
            return fragmentClass.equals(fragment.getClass());
        }

        State(Class<? extends Fragment> fragmentClass, int id, Bundle args, Bundle savedState, SparseArray<Parcelable> viewState) {
            this.fragmentClass = fragmentClass;
            this.id = id;
            this.args = args;
            this.savedState = savedState;
            this.viewState = viewState;
        }

        @SuppressWarnings("unchecked")
        State(Parcel in) {
            try {
                fragmentClass = (Class<? extends Fragment>) Class.forName(in.readString());
                id = in.readInt();
                args = in.readBundle(getClass().getClassLoader());
                savedState = in.readBundle(getClass().getClassLoader());
                viewState = in.readSparseArray(getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(fragmentClass.getName());
            dest.writeInt(id);
            dest.writeBundle(args);
            dest.writeBundle(savedState);
            dest.writeSparseArray(viewState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }

    public interface RestoreStateFactory<T extends Fragment> {
        T create();
    }
}
