package me.tatarka.betterfragment.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;
import me.tatarka.betterfragment.app.FragmentOwners;
import me.tatarka.betterfragment.transition.FragmentTransition;

@Navigator.Name("fragment")
public class FragmentNavigator extends OptimizingNavigator<FragmentNavigator.Destination, FragmentNavigator.Page, FragmentNavigator.PageState> {

    private final FragmentManager fm;
    private final FrameLayout container;
    private Fragment.Factory fragmentFactory = Fragment.DefaultFactory.getInstance();

    public FragmentNavigator(FrameLayout container) {
        this.container = container;
        fm = new FragmentManager(FragmentOwners.get(container));
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
    protected Page createPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Extras navExtras) {
        Fragment fragment = destination.newFragment(args);
        fragment.setArgs(args);
        return new Page(fragmentFactory, fragment, navOptions);
    }

    @Override
    protected void replace(@Nullable Page oldPage, @NonNull Page newPage, int backStackEffect) {
        Fragment oldFragment = oldPage != null ? oldPage.fragment : null;
        Fragment newFragment = newPage.fragment;
        int enterAnim = -1;
        int exitAnim = -1;
        switch (backStackEffect) {
            case Navigator.BACK_STACK_DESTINATION_ADDED:
                enterAnim = newPage.enterAnim;
                exitAnim = newPage.exitAnim;
                break;
            case Navigator.BACK_STACK_DESTINATION_POPPED:
                if (oldPage != null) {
                    enterAnim = oldPage.popEnterAnim;
                    exitAnim = oldPage.popExitAnim;
                }
                break;
        }
        FragmentTransition transition = FragmentTransition.fromAnimRes(container.getContext(), enterAnim, exitAnim);
        fm.replace(oldFragment, newFragment, container, transition);
    }

    @NonNull
    @Override
    protected PageState savePageState(Page page) {
        return page.saveState(fm);
    }

    @NonNull
    @Override
    protected Page restorePageState(PageState state) {
        return state.restore(fm, fragmentFactory);
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
        public <T extends Fragment> T newFragment(@Nullable Bundle args) {
            try {
                return fragmentFactory.newInstance(name, args);
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

    static class Page {
        final Fragment.Factory factory;
        final Fragment fragment;
        final int enterAnim;
        final int exitAnim;
        final int popEnterAnim;
        final int popExitAnim;

        Page(Fragment.Factory factory, Fragment fragment, @Nullable NavOptions navOptions) {
            this.factory = factory;
            this.fragment = fragment;
            enterAnim = navOptions != null ? navOptions.getEnterAnim() : 0;
            exitAnim = navOptions != null ? navOptions.getExitAnim() : 0;
            popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : 0;
            popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : 0;
        }

        Page(Fragment.Factory factory, Fragment fragment, PageState state) {
            this.factory = factory;
            this.fragment = fragment;
            enterAnim = 0;
            exitAnim = 0;
            popEnterAnim = state.popEnterAnim;
            popExitAnim = state.popExitAnim;
        }

        PageState saveState(FragmentManager fm) {
            return new PageState(fragment.getClass().getName(), fm.saveState(fragment), popEnterAnim, popExitAnim);
        }
    }

    static class PageState implements Parcelable {
        final String name;
        final Fragment.State state;
        final int popEnterAnim;
        final int popExitAnim;

        PageState(String name, Fragment.State state, int popEnterAnim, int popExitAnim) {
            this.name = name;
            this.state = state;
            this.popEnterAnim = popEnterAnim;
            this.popExitAnim = popExitAnim;
        }

        PageState(Parcel in) {
            name = in.readString();
            state = in.readParcelable(Fragment.State.class.getClassLoader());
            popEnterAnim = in.readInt();
            popExitAnim = in.readInt();
        }

        Page restore(FragmentManager fm, Fragment.Factory factory) {
            Fragment fragment = factory.newInstance(name, state.getArgs());
            fm.restoreState(fragment, state);
            return new Page(factory, fragment, this);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeParcelable(state, flags);
            dest.writeInt(popEnterAnim);
            dest.writeInt(popExitAnim);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PageState> CREATOR = new Creator<PageState>() {
            @Override
            public PageState createFromParcel(Parcel in) {
                return new PageState(in);
            }

            @Override
            public PageState[] newArray(int size) {
                return new PageState[size];
            }
        };
    }
}
