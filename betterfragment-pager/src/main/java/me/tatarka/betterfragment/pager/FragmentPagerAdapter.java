package me.tatarka.betterfragment.pager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelStore;
import androidx.viewpager.widget.PagerAdapter;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentOwner;
import me.tatarka.betterfragment.FragmentOwners;

public abstract class FragmentPagerAdapter extends PagerAdapter {

    private static final String STATE_FRAGMENTS = "fragments";

    private final FragmentOwner owner;
    private int primaryPage = -1;
    private SparseArray<Page> pages = new SparseArray<>();
    private SparseArray<Fragment.State> pageState = new SparseArray<>();

    public FragmentPagerAdapter(Context context) {
        this(FragmentOwners.get(context));
    }

    public FragmentPagerAdapter(FragmentOwner owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    @CallSuper
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment.State state = pageState.get(position);
        Page page = new Page(owner, container, position, state);
        pages.put(position, page);
        return page;
    }

    @Override
    @CallSuper
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Page page = (Page) object;
        pages.remove(position);
        pageState.put(position, page.destroy());
        container.removeView(page.fragment.getView());
    }

    @Override
    public final int getItemPosition(@NonNull Object object) {
        Page page = (Page) object;
        return getItemForPosition(page.fragment);
    }

    public final int getItemForPosition(@NonNull Fragment fragment) {
        return fragment.getId();
    }

    @Override
    @CallSuper
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (position != primaryPage) {
            if (primaryPage != -1) {
                pages.get(primaryPage).setPrimary(false);
            }
            Page page = (Page) object;
            page.setPrimary(true);
            primaryPage = position;
        }
    }

    @NonNull
    public abstract Fragment getItem(int position);

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((Page) object).fragment.getView();
    }

    @Override
    @CallSuper
    public void restoreState(Parcelable state, @Nullable ClassLoader loader) {
        Bundle bundle = (Bundle) state;
        bundle.setClassLoader(getClass().getClassLoader());
        pageState = bundle.getSparseParcelableArray(STATE_FRAGMENTS);
    }

    @Override
    @CallSuper
    public Parcelable saveState() {
        Bundle state = new Bundle();
        for (int i = 0; i < pages.size(); i++) {
            pageState.put(pages.keyAt(i), pages.valueAt(i).saveState());
        }
        state.putSparseParcelableArray(STATE_FRAGMENTS, pageState);
        return state;
    }

    class Page implements FragmentOwner, LifecycleObserver {
        private final FragmentOwner parentOwner;
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        final Fragment fragment;
        final int position;
        private boolean isPrimary;
        private boolean isReallyResumed;

        Page(FragmentOwner parentOwner, ViewGroup container, final int position, @Nullable final Fragment.State savedState) {
            this.parentOwner = parentOwner;
            fragment = getItem(position);
            if (savedState == null) {
                fragment.create(this, container, position);
            } else {
                fragment.create(this, container, savedState);
            }
            this.position = position;
            parentOwner.getLifecycle().addObserver(this);
        }

        Fragment.State destroy() {
            parentOwner.getLifecycle().removeObserver(this);
            Fragment.State state = fragment.saveState();
            fragment.destroy();
            return state;
        }

        Fragment.State saveState() {
            return fragment.saveState();
        }

        void setPrimary(boolean value) {
            isPrimary = value;
            if (isReallyResumed) {
                if (isPrimary) {
                    lifecycleRegistry.markState(Lifecycle.State.RESUMED);
                } else {
                    lifecycleRegistry.markState(Lifecycle.State.STARTED);
                }
            }
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return lifecycleRegistry;
        }

        @NonNull
        @Override
        public ViewModelStore getViewModelStore() {
            return parentOwner.getViewModelStore();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume() {
            isReallyResumed = true;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause() {
            isReallyResumed = false;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        void onLifecycleEvent(LifecycleOwner source, Lifecycle.Event event) {
            if (isPrimary || (event != Lifecycle.Event.ON_PAUSE && event != Lifecycle.Event.ON_RESUME)) {
                lifecycleRegistry.handleLifecycleEvent(event);
            }
        }

        @Override
        public boolean willRestoreState() {
            return owner.willRestoreState();
        }
    }
}
