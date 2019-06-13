package me.tatarka.shard.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;

import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;

public class ShardNavHost extends FrameLayout implements NavHost {

    private NavHostController navController;
    private ShardOwner owner;
    private int graphId;

    public ShardNavHost(Context context) {
        this(context, null);
    }

    public ShardNavHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            owner = ShardOwners.get(context);
        }
        navController = new NavHostController(context);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShardNavHost);
            graphId = a.getResourceId(R.styleable.ShardNavHost_navGraph, 0);
            if (isInEditMode()) {
                int layout = a.getResourceId(R.styleable.ShardNavHost_layout, 0);
                if (layout != 0) {
                    inflate(context, layout, this);
                }
            }
            a.recycle();
            Navigation.setViewNavController(this, navController);
            ShardNavigator shardNavigator = new ShardNavigator(this);
            navController.getNavigatorProvider().addNavigator(shardNavigator);
        }
        if (!isInEditMode()) {
            if (graphId != 0 && !ShardManager.isRestoringState(owner)) {
                navController.setGraph(graphId);
            }

            ViewLifecycleOwner lifecycleOwner = new ViewLifecycleOwner(owner);
            navController.setLifecycleOwner(lifecycleOwner);
            navController.setViewModelStore(owner.getViewModelStore());
            navController.setOnBackPressedDispatcher(owner.getOnBackPressedDispatcher());

            addOnAttachStateChangeListener(lifecycleOwner);
        }
    }

    public void setGraph(NavGraph graph) {
        navController.setGraph(graph);
    }

    public void setGraph(@NavigationRes int graphId) {
        navController.setGraph(graphId);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), navController.saveState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        navController.restoreState(savedState.navState);
        if (graphId != 0) {
            navController.setGraph(graphId);
        }
    }

    @NonNull
    @Override
    public NavController getNavController() {
        return navController;
    }

    public static class SavedState extends BaseSavedState {
        final Bundle navState;

        SavedState(Parcel source) {
            super(source);
            navState = source.readBundle(getClass().getClassLoader());
        }

        SavedState(Parcelable superState, Bundle navState) {
            super(superState);
            this.navState = navState;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(navState);
        }

        public static Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Wraps the parent's lifecycle so that this lifecycle is stopped when the view is detached.
     */
    static class ViewLifecycleOwner implements
            LifecycleOwner,
            LifecycleEventObserver,
            OnAttachStateChangeListener {
        private final ShardOwner owner;
        private final LifecycleRegistry registry = new LifecycleRegistry(this);
        private boolean isAttached;

        ViewLifecycleOwner(ShardOwner owner) {
            this.owner = owner;
            owner.getLifecycle().addObserver(this);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return registry;
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (isAttached) {
                registry.handleLifecycleEvent(event);
            }
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            isAttached = true;
            registry.setCurrentState(owner.getLifecycle().getCurrentState());
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            isAttached = false;
            registry.setCurrentState(Lifecycle.State.CREATED);
        }
    }
}
