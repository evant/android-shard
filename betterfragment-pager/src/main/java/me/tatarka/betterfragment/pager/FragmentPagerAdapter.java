package me.tatarka.betterfragment.pager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

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
import androidx.viewpager.widget.ViewPager;
import me.tatarka.betterfragment.app.ActivityCallbacks;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;
import me.tatarka.betterfragment.app.FragmentOwner;
import me.tatarka.betterfragment.app.FragmentOwners;

/**
 * Implementation of {@link PagerAdapter} that represents each page as a {@link Fragment}.
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 *
 * <p>
 * {@link ViewPager#getOffscreenPageLimit()} number of fragments will be kept in memory. Otherwise
 * their state will be saved and the fragment destroyed. The current fragment will be in the resumed
 * state and all other fragments will be in the started state. You must override
 * {@link #getItemPosition(Fragment)} if you want to be able to dynamically change the contents on a
 * {@link #notifyDataSetChanged()}.
 */
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
        Fragment fragment = getItem(position);
        Page page = new Page(owner, container, fragment, state);
        pages.put(position, page);
        return page;
    }

    @Override
    @CallSuper
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Page page = (Page) object;
        if (pages.indexOfKey(position) >= 0) {
            pages.remove(position);
            pageState.put(position, page.saveState());
        }
        page.destroy();
        container.removeView(page.fragment.getView());
    }

    @Override
    public final int getItemPosition(@NonNull Object object) {
        Page page = (Page) object;
        int position = getItemPosition(page.fragment);
        if (position == POSITION_UNCHANGED) {
            return position;
        }
        int index = pages.indexOfValue(page);
        pages.removeAt(index);
        if (position != POSITION_NONE) {
            pages.put(position, page);
        }
        return position;
    }

    /**
     * Called when the host view is attempting to determine if an item's position
     * has changed. Returns {@link #POSITION_UNCHANGED} if the position of the given
     * item has not changed or {@link #POSITION_NONE} if the item is no longer present
     * in the adapter.
     *
     * <p>The default implementation assumes that items will never
     * change position and always returns {@link #POSITION_UNCHANGED}.
     *
     * @param fragment Fragment representing an item, previously returned by a call to
     *                 {@link #instantiateItem(View, int)}.
     * @return fragment's new position index from [0, {@link #getCount()}),
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * or {@link #POSITION_NONE} if the item is no longer present.
     */
    public int getItemPosition(Fragment fragment) {
        return POSITION_UNCHANGED;
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
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
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

    static class Page implements FragmentOwner, LifecycleObserver {
        private final FragmentOwner parentOwner;
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        private final FragmentManager fm;
        final Fragment fragment;
        private boolean isPrimary;
        private boolean isReallyResumed;

        Page(FragmentOwner parentOwner, ViewGroup container, Fragment fragment, @Nullable Fragment.State state) {
            this.parentOwner = parentOwner;
            this.fragment = fragment;
            fm = new FragmentManager(this);
            fm.restoreState(fragment, state);
            fm.add(fragment, container);
            parentOwner.getLifecycle().addObserver(this);
        }

        void destroy() {
            parentOwner.getLifecycle().removeObserver(this);
            fm.remove(fragment);
        }

        Fragment.State saveState() {
            return fm.saveState(fragment);
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
            return parentOwner.willRestoreState();
        }
    }
}
