package me.tatarka.betterfragment.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import me.tatarka.betterfragment.DefaultFragmentFactory;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentFactory;
import me.tatarka.betterfragment.FragmentOwner;
import me.tatarka.betterfragment.FragmentOwners;

@Navigator.Name("fragment")
public class FragmentNavigator extends OptomizingNavigator<FragmentNavigator.Destination, Fragment, Fragment.State> {

    private final FragmentOwner owner;
    private final ViewGroup container;
    private final FragmentFactory fragmentFactory;

    public FragmentNavigator(ViewGroup container) {
        this(container, DefaultFragmentFactory.getInstance());
    }

    public FragmentNavigator(ViewGroup container, FragmentFactory fragmentFactory) {
        this.owner = FragmentOwners.get(container);
        this.container = container;
        this.fragmentFactory = fragmentFactory;
    }

    @NonNull
    @Override
    public Destination createDestination() {
        return new Destination(this, fragmentFactory);
    }

    @Override
    public Fragment newPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        return destination.newFragment(args);
    }

    @Override
    public void replace(@Nullable Fragment oldPage, @Nullable Fragment newPage, @Nullable Fragment.State newState, int backStackEffect) {
        if (oldPage != null) {
            oldPage.destroy();
        }
        if (newPage != null) {
            if (newState != null) {
                newPage.create(owner, container, newState);
            } else {
                newPage.create(owner, container, newPage.getId());
            }
        }
    }

    @Override
    public Fragment.State savePageState(Fragment fragment) {
        return fragment.saveState();
    }

    @Override
    public Fragment restorePageState(final Fragment.State state) {
        return fragmentFactory.newInstance(state.getFragmentClass());
    }

    public static class Destination extends NavDestination {

        private String name;
        private FragmentFactory fragmentFactory;
        private Fragment fragment;

        Destination(Navigator<? extends NavDestination> navigator, FragmentFactory fragmentFactory) {
            super(navigator);
            this.fragmentFactory = fragmentFactory;
        }

        public String getName() {
            return name;
        }

        public Fragment getFragment() {
            return fragment;
        }

        @Override
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentNavigator);
            name = a.getString(R.styleable.FragmentNavigator_android_name);
            a.recycle();
        }

        @SuppressWarnings("unchecked")
        <T extends Fragment> T newFragment(Bundle args) {
            try {
                fragment = fragmentFactory.newInstance((Class<T>) Class.forName(name));
                fragment.setArgs(args);
                return (T) fragment;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
