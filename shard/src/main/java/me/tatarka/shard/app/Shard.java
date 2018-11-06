package me.tatarka.shard.app;

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
import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.content.ComponentCallbacks;
import me.tatarka.shard.content.ComponentCallbacksDispatcher;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.state.InstanceStateStore;

/**
 * A simpler 'Shard' that lies on the android architecture components for most of the heavy-lifting.
 * <p>
 * It is a {@link LifecycleOwner} and {@link androidx.lifecycle.ViewModelStoreOwner}.
 */
public class Shard implements ShardOwner {

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private final InstanceStateRegistry stateStore = new InstanceStateRegistry();
    private NestedActivityCallbacksDispatcher activityCallbackDispatcher;
    private ComponentCallbacksDispatcher componentCallbacksDispatcher;
    private final Observer observer = new Observer();
    private int viewModelId = -1;
    private Container container;
    @Nullable
    private FrameLayout frame;
    private ShardOwner owner;
    private Context context;
    private Bundle args;
    private boolean destroyed;
    @Nullable
    private State state;

    void restoreState(@Nullable State state) {
        checkNotCreated();
        this.state = state;
        if (state != null) {
            if (state.args != null) {
                this.args = state.args;
            }
            viewModelId = state.viewModelId;
        }
    }

    void add(@NonNull ShardOwner owner, @NonNull Container container) {
        checkNotCreated();
        if (destroyed) {
            return;
        }
        this.owner = owner;
        context = new ShardOwnerContextWrapper(owner.getContext(), this);
        activityCallbackDispatcher = new NestedActivityCallbacksDispatcher((BaseActivityCallbacksDispatcher) owner.getActivityCallbacks(), this);
        componentCallbacksDispatcher = new ComponentCallbacksDispatcher(this);
        this.container = container;

        if (state != null && state.savedState != null) {
            stateStore.onRestoreInstanceState(state.savedState);
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
    Shard.State saveInstanceState() {
        checkDestroyed();
        State state = new State(viewModelId, args);
        if (lifecycleRegistry.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
            if (frame != null) {
                state.viewState = new SparseArray();
                frame.saveHierarchyState(state.viewState);
            }
            state.savedState = stateStore.onSaveInstanceState();
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
            throw new IllegalStateException("Shard is already destroyed");
        }
    }

    private void checkCreated() {
        if (owner == null) {
            throw new IllegalStateException("Shard is not created");
        }
    }

    private void checkNotCreated() {
        if (this.owner != null) {
            throw new IllegalStateException("Shard is already created");
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

    /**
     * Called when the shard is created, it's safe to call most method on the shard after
     * this.
     */
    public void onCreate() {
    }

    @NonNull
    @Override
    public final Context getContext() {
        checkCreated();
        return context;
    }

    @NonNull
    @Override
    public Factory getShardFactory() {
        checkCreated();
        return owner.getShardFactory();
    }

    @NonNull
    @Override
    public ActivityCallbacks getActivityCallbacks() {
        checkCreated();
        return activityCallbackDispatcher;
    }

    @NonNull
    @Override
    public ComponentCallbacks getComponentCallbacks() {
        checkCreated();
        return componentCallbacksDispatcher;
    }

    private ViewModelStore getOrCreateViewModelStore() {
        ShardManagerViewModel viewModel = ShardManagerViewModel.get(owner.getViewModelStore());
        if (viewModelId == -1) {
            viewModelId = viewModel.nextId();
        }
        return viewModel.get(viewModelId);
    }

    private void removeViewModelStore(int id) {
        ShardManagerViewModel.get(owner.getViewModelStore()).remove(id);
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
    public InstanceStateStore getInstanceStateStore() {
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
        final int viewModelId;
        @Nullable
        final Bundle args;
        Bundle savedState;
        SparseArray viewState;

        /**
         * Returns the arguments for this shard, you can only read these, you cannot modify them.
         */
        @NonNull
        public Bundle getArgs() {
            return args != null ? args : Bundle.EMPTY;
        }

        State(int viewModelId, @Nullable Bundle args) {
            this.viewModelId = viewModelId;
            this.args = args;
        }

        @SuppressWarnings("unchecked")
        State(Parcel in) {
            viewModelId = in.readInt();
            args = in.readBundle(getClass().getClassLoader());
            savedState = in.readBundle(getClass().getClassLoader());
            viewState = in.readSparseArray(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
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

    /**
     * Factory to create a shard from the given class name and args.
     */
    public interface Factory {
        /**
         * Constructs a new instance of the shard with the given arguments.
         *
         * @param name The shard's class name.
         * @param args The args for the shard. You may pass {@link Bundle#EMPTY} if there are
         *             none.
         */
        @NonNull
        <T extends Shard> T newInstance(@NonNull String name, @NonNull Bundle args);
    }

    /**
     * The default {@link Factory} which calls the zero-arg constructor of the shard using
     * reflection. This does <em>not</em> call {@link #setArgs(Bundle)}.
     */
    public static class DefaultFactory implements Factory {

        private static final Factory INSTANCE = new DefaultFactory();

        @NonNull
        public static Factory getInstance() {
            return INSTANCE;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends Shard> T newInstance(@NonNull String name, @NonNull Bundle args) {
            try {
                return (T) Class.forName(name).getConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
