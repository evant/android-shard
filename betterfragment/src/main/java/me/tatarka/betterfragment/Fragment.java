package me.tatarka.betterfragment;

import android.content.Context;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelStore;

public class Fragment implements FragmentOwner {

    /**
     * Constructs a new fragment instance from the given class using reflection.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T newInstance(Class<T> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final Observer observer = new Observer();
    private int viewModelId = -1;
    private ViewGroup frame;
    private FragmentOwner owner;
    private Context context;
    private Bundle args;
    private boolean destroyed;
    private boolean willRestoreState;

    /**
     * Sets of the fragment, attaching is to the given container and calling {@link #onCreate(Bundle)}.
     * This method is a no-op if the fragment is already destroyed.
     */
    public void create(@NonNull FragmentOwner owner, @NonNull ViewGroup container) {
        create(owner, container, null);
    }

    /**
     * Sets of the fragment, attaching is to the given container and calling {@link #onCreate(Bundle)}.
     * If state is not null, it will restore itself from the given state. This method is a no-op if
     * the fragment is already destroyed.
     *
     * @throws IllegalStateException    If the fragment has already been created or has been created.
     * @throws IllegalArgumentException If the state is not for this fragment.
     */
    public void create(@NonNull FragmentOwner owner, @NonNull ViewGroup container, @Nullable State state) {
        if (this.owner != null) {
            throw new IllegalStateException("Fragment is already created");
        }
        if (state != null && !state.isFor(this)) {
            throw new IllegalArgumentException("wrong state for fragment: " + toString());
        }
        if (destroyed) {
            return;
        }
        this.owner = owner;
        if (state != null) {
            if (state.args != null) {
                this.args = state.args;
            }
            viewModelId = state.viewModelId;
        }
        context = new FragmentContextWrapper(container.getContext(), this);
        frame = new FrameLayout(context);
        frame.setSaveFromParentEnabled(false);
        container.addView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        willRestoreState = state != null;
        onCreate(state != null ? state.savedState : null);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        if (state != null && state.viewState != null) {
            frame.restoreHierarchyState(state.viewState);
        }
        owner.getLifecycle().addObserver(observer);
    }

    /**
     * Saves the fragment's state and returns it. This will move the fragment to the stopped state
     * so that when {@link #onSaveInstanceState(Bundle)} is called is consistent. Therefore you
     * should only call this method when the fragment is being stopped or destroyed.
     *
     * @throws IllegalStateException If the fragment is destroyed.
     */
    @NonNull
    public Fragment.State saveState() {
        checkDestroyed();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        SparseArray<Parcelable> viewState = new SparseArray<>();
        if (frame != null) {
            frame.saveHierarchyState(viewState);
        }
        State state = new State(getClass(), viewModelId, args, new Bundle(), viewState);
        onSaveInstanceState(state.savedState);
        return state;
    }

    /**
     * Destroys the fragment. After this most operations on this instance will throw an exception.
     * Note: you should not call this on configuration changes, only when you are actually done with
     * it.
     *
     * @throws IllegalStateException If the fragment is already destroyed.
     */
    public void destroy() {
        checkDestroyed();
        destroyed = true;
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        removeViewModelStore(viewModelId);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        owner.getLifecycle().removeObserver(observer);
        ((ViewGroup) frame.getParent()).removeView(frame);
    }

    private void checkDestroyed() {
        if (destroyed) {
            throw new IllegalStateException("Fragment is already destroyed");
        }
    }

    private void checkCreated() {
        if (owner == null) {
            throw new IllegalStateException("Fragment is not created");
        }
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

    @NonNull
    public final ViewGroup getView() {
        checkCreated();
        return frame;
    }

    public void setContentView(@NonNull View view) {
        ViewGroup frame = getView();
        frame.removeAllViews();
        frame.addView(view);
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @NonNull
    public final <T extends View> T requireViewById(@IdRes int id) {
        T view = findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this View");
        }
        return view;
    }

    public void setContentView(@LayoutRes int layoutId) {
        LayoutInflater.from(getContext()).inflate(layoutId, getView(), true);
    }

    @CallSuper
    public void onCreate(@Nullable Bundle savedState) {
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }

    @NonNull
    public final Context getContext() {
        checkCreated();
        return context;
    }

    private ViewModelStore getOrCreateViewModelStore() {
        FragmentManagerViewModel viewModel = FragmentManagerViewModel.get(owner.getViewModelStore());
        if (viewModelId == -1) {
            viewModelId = viewModel.nextId();
        }
        return viewModel.get(viewModelId);
    }

    private void removeViewModelStore(int id) {
        FragmentManagerViewModel.get(owner.getViewModelStore()).remove(id);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        checkCreated();
        return getOrCreateViewModelStore();
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
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        void onLifecyleEvent(LifecycleOwner owner, Lifecycle.Event event) {
            lifecycleRegistry.handleLifecycleEvent(event);
            if (event == Lifecycle.Event.ON_DESTROY) {
                owner.getLifecycle().removeObserver(this);
            }
        }
    }

    public static class State implements Parcelable {
        final Class<? extends Fragment> fragmentClass;
        final int viewModelId;
        final Bundle args;
        final Bundle savedState;
        final SparseArray viewState;

        @NonNull
        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public boolean isFor(@NonNull Fragment fragment) {
            return fragmentClass.equals(fragment.getClass());
        }

        State(Class<? extends Fragment> fragmentClass, int viewModelId, Bundle args, Bundle savedState, SparseArray<Parcelable> viewState) {
            this.fragmentClass = fragmentClass;
            this.viewModelId = viewModelId;
            this.args = args;
            this.savedState = savedState;
            this.viewState = viewState;
        }

        @SuppressWarnings("unchecked")
        State(Parcel in) {
            try {
                fragmentClass = (Class<? extends Fragment>) Class.forName(in.readString());
                viewModelId = in.readInt();
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
            dest.writeInt(viewModelId);
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

    public interface Factory {
        @NonNull
        <T extends Fragment> T newInstance(@NonNull Class<T> fragmentClass);
    }
}
