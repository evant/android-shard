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
import me.tatarka.betterfragment.FragmentManager;
import me.tatarka.betterfragment.FragmentOwners;

@Navigator.Name("fragment")
public class FragmentNavigator extends OptimizingNavigator<FragmentNavigator.Destination, Fragment, Fragment.State> {

    private final FragmentManager fm;
    private final ViewGroup container;
    private Fragment.Factory fragmentFactory = DefaultFragmentFactory.getInstance();

    public FragmentNavigator(ViewGroup container) {
        this.fm = new FragmentManager(FragmentOwners.get(container));
        this.container = container;
    }

    public void setFragmentFactory(@NonNull Fragment.Factory factory) {
        fragmentFactory = factory;
    }

    @NonNull
    public Fragment.Factory getFragmentFactory() {
        return fragmentFactory;
    }

    @NonNull
    @Override
    public Destination createDestination() {
        return new Destination(this, fragmentFactory);
    }

    @NonNull
    @Override
    protected Fragment createPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        Fragment fragment = destination.newFragment();
        fragment.setArgs(args);
        return fragment;
    }

    @Override
    protected void replace(@Nullable Fragment oldPage, @Nullable Fragment newPage, int backStackEffect) {
        if (oldPage != null) {
            fm.remove(oldPage);
        }
        if (newPage != null) {
            fm.add(newPage, container);
        }
    }

    @NonNull
    @Override
    protected Fragment.State savePageState(Fragment fragment) {
        return fm.saveState(fragment);
    }

    @NonNull
    @Override
    protected Fragment restorePageState(final Fragment.State state) {
        Fragment fragment = fragmentFactory.newInstance(state.getFragmentClass());
        fm.restoreState(fragment, state);
        return fragment;
    }

    public static class Destination extends NavDestination {

        private String name;
        private Fragment.Factory fragmentFactory;

        Destination(Navigator<? extends NavDestination> navigator, Fragment.Factory fragmentFactory) {
            super(navigator);
            this.fragmentFactory = fragmentFactory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public <T extends Fragment> T newFragment() {
            try {
                return fragmentFactory.newInstance((Class<T>) Class.forName(name));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentNavigator);
            name = a.getString(R.styleable.FragmentNavigator_android_name);
            a.recycle();
        }
    }
}
