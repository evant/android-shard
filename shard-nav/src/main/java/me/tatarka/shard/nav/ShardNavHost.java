package me.tatarka.shard.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ContentView;
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavHost;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;

public class ShardNavHost extends FrameLayout implements NavHost {

    private NavController navController;
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
        navController = new NavController(context);
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
            NavCallbacks navCallbacks = new NavCallbacks(owner, navController);
            addOnAttachStateChangeListener(navCallbacks);
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

    static class NavCallbacks implements OnAttachStateChangeListener, OnBackPressedCallback {
        final ActivityCallbacks callbacks;
        final NavController navController;

        NavCallbacks(ShardOwner owner, NavController navController) {
            this.callbacks = owner.getActivityCallbacks();
            this.navController = navController;
        }

        @Override
        public boolean handleOnBackPressed() {
            return navController.popBackStack();
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            callbacks.addOnBackPressedCallback(this);
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            callbacks.removeOnBackPressedCallback(this);
        }
    }
}
