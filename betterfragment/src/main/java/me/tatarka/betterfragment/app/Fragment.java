package me.tatarka.betterfragment.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationTargetException;

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
import me.tatarka.betterfragment.state.StateStore;

/**
 * A simpler 'Fragment' that lies on the android architecture components for most of the heavy-lifting.
 * <p>
 * It is a {@link LifecycleOwner} and {@link androidx.lifecycle.ViewModelStoreOwner}.
 */
public class Fragment implements FragmentOwner {

    /**
     * Constructs a new fragment instance from the given class using reflection.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T newInstance(Class<T> fragmentClass) {
        try {
            return fragmentClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final StateStore stateStore = new StateStore();
    private final Observer observer = new Observer();
    private int viewModelId = -1;
    private Container container;
    @Nullable
    private FrameLayout frame;
    private FragmentOwner owner;
    private Context context;
    private Bundle args;
    private boolean destroyed;
    @Nullable
    private State state;

    void restoreState(@Nullable State state) {
        checkNotCreated();
        if (state != null && !state.isFor(this)) {
            throw new IllegalArgumentException("wrong state for fragment: " + toString());
        }
        this.state = state;
        if (state != null) {
            if (state.args != null) {
                this.args = state.args;
            }
            viewModelId = state.viewModelId;
        }
    }

    void add(@NonNull FragmentOwner owner, @NonNull Container container) {
        checkNotCreated();
        if (destroyed) {
            return;
        }
        this.owner = owner;
        context = new FragmentOwnerContextWrapper(owner.getContext(), this);
        this.container = container;

        if (state != null && state.savedState != null) {
            stateStore.onRestoreState(state.savedState);
        }
        onCreate();

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

        owner.getLifecycle().addObserver(observer);
        state = null;
    }

    public interface Container {
        void addView(View view);

        void removeView(View view);
    }

    @NonNull
    Fragment.State saveState() {
        checkDestroyed();
        State state = new State(getClass(), viewModelId, args);
        if (lifecycleRegistry.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
            if (frame != null) {
                state.viewState = new SparseArray();
                frame.saveHierarchyState(state.viewState);
            }
            state.savedState = new Bundle();
            stateStore.onSaveState(state.savedState);
        }
        return state;
    }

    void remove() {
        checkDestroyed();
        destroyed = true;
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        removeViewModelStore(viewModelId);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        owner.getLifecycle().removeObserver(observer);

        if (container != null && frame != null) {
            container.removeView(frame);
        }
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

    private void checkNotCreated() {
        if (this.owner != null) {
            throw new IllegalStateException("Fragment is already created");
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

    @CallSuper
    public void setContentView(@NonNull View view) {
        checkCreated();
        ViewGroup frame = createFrame();
        frame.removeAllViews();
        frame.addView(view);
        restoreViewState(frame);
    }

    @CallSuper
    public void setContentView(@LayoutRes int layoutId) {
        checkCreated();
        ViewGroup frame = createFrame();
        frame.removeAllViews();
        LayoutInflater.from(context).inflate(layoutId, frame, true);
        restoreViewState(frame);
    }

    private ViewGroup createFrame() {
        if (frame == null) {
            frame = new FrameLayout(context);
            frame.setSaveFromParentEnabled(false);
            container.addView(frame);
        }
        return frame;
    }

    public ViewGroup getView() {
        return frame;
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        return frame != null ? frame.<T>findViewById(id) : null;
    }

    @NonNull
    public final <T extends View> T requireViewById(@IdRes int id) {
        T view = findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this View");
        }
        return view;
    }

    private void restoreViewState(View frame) {
        if (state != null && state.viewState != null) {
            frame.restoreHierarchyState(state.viewState);
        }
    }

    public void onCreate() {
    }

    @NonNull
    @Override
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

    @NonNull
    @Override
    public StateStore getStateStore() {
        return stateStore;
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
        Bundle savedState;
        SparseArray viewState;

        @NonNull
        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public boolean isFor(@NonNull Fragment fragment) {
            return fragmentClass.equals(fragment.getClass());
        }

        State(Class<? extends Fragment> fragmentClass, int viewModelId, Bundle args) {
            this.fragmentClass = fragmentClass;
            this.viewModelId = viewModelId;
            this.args = args;
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

    public static class DefaultFactory implements Factory {

        private static final Factory INSTANCE = new DefaultFactory();

        @NonNull
        public static Factory getInstance() {
            return INSTANCE;
        }

        @NonNull
        @Override
        public <T extends Fragment> T newInstance(@NonNull Class<T> fragmentClass) {
            return Fragment.newInstance(fragmentClass);
        }
    }
}
