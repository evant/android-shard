package me.tatarka.betterfragment.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentOwner;
import me.tatarka.betterfragment.app.FragmentOwners;

public class FragmentNavHost extends FrameLayout implements NavHost {

    private final NavController navController;
    private final FragmentOwner owner;
    private int graphId = 0;

    public FragmentNavHost(Context context) {
        this(context, null);
    }

    public FragmentNavHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        owner = FragmentOwners.get(this);
        navController = new NavController(context);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentNavHost);
            graphId = a.getResourceId(R.styleable.FragmentNavHost_graphId, View.NO_ID);
            a.recycle();
            Navigation.setViewNavController(this, navController);
            FragmentNavigator fragmentNavigator = new FragmentNavigator(this);
            navController.getNavigatorProvider().addNavigator(fragmentNavigator);
        }
    }

    public void setGraph(NavGraph graph) {
        navController.setGraph(graph);
    }

    public void setGraph(@NavigationRes int graphId) {
        navController.setGraph(graphId);
    }

    public void setFragmentFactory(Fragment.Factory factory) {
        navController.getNavigatorProvider().getNavigator(FragmentNavigator.class).setFragmentFactory(factory);
    }

    public Fragment.Factory getFragmentFactory() {
        return navController.getNavigatorProvider().getNavigator(FragmentNavigator.class).getFragmentFactory();
    }

    @Override
    @CallSuper
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (navController.getGraph() == null && graphId != 0 && !owner.getInstanceStateStore().isStateRestored()) {
            navController.setGraph(graphId);
        }
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
}
