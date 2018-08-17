package me.tatarka.betterfragment.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import me.tatarka.betterfragment.DefaultFragmentFactory;
import me.tatarka.betterfragment.FragmentFactory;
import me.tatarka.betterfragment.FragmentOwner;
import me.tatarka.betterfragment.FragmentOwners;

public class FragmentNavHost extends FrameLayout implements NavHost {

    private final NavController navController;

    public FragmentNavHost(Context context, NavGraph graph) {
        this(context, graph, DefaultFragmentFactory.getInstance());
    }

    public FragmentNavHost(Context context, NavGraph graph, FragmentFactory fragmentFactory) {
        super(context);
        navController = new NavController(context);
        init(fragmentFactory);
        navController.setGraph(graph);
    }

    public FragmentNavHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        navController = new NavController(context);
        FragmentFactory factory = DefaultFragmentFactory.getInstance();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentNavHost);
        int graphId = a.getResourceId(R.styleable.FragmentNavHost_graphId, View.NO_ID);
        String factoryName = a.getString(R.styleable.FragmentNavHost_fragmentFactory);
        if (factoryName != null) {
            try {
                factory = (FragmentFactory) Class.forName(factoryName).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        a.recycle();
        init(factory);
        FragmentOwner owner = FragmentOwners.get(this);
        if (!owner.willRestoreState()) {
            navController.setGraph(graphId);
        }
    }

    private void init(FragmentFactory fragmentFactory) {
        Navigation.setViewNavController(this, navController);
        FragmentNavigator fragmentNavigator = new FragmentNavigator(this, fragmentFactory);
        navController.getNavigatorProvider().addNavigator(fragmentNavigator);
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
