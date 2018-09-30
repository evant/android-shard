package me.tatarka.shard.pager;

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
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwnerContextWrapper;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.state.InstanceStateStore;

/**
 * Implementation of {@link PagerAdapter} that represents each page as a {@link Shard}.
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 *
 * <p>
 * {@link ViewPager#getOffscreenPageLimit()} number of shards will be kept in memory. Otherwise
 * their state will be saved and the shard destroyed. The current shard will be in the resumed
 * state and all other shards will be in the started state. You must override
 * {@link #getItemPosition(Shard)} if you want to be able to dynamically change the contents on a
 * {@link #notifyDataSetChanged()}.
 */
public abstract class ShardPagerAdapter extends PagerAdapter {

    private static final String STATE_SHARDS = "shards";

    private final ShardOwner owner;
    private int primaryPage = -1;
    private SparseArray<Page> pages = new SparseArray<>();
    private SparseArray<Shard.State> pageState = new SparseArray<>();

    public ShardPagerAdapter(Context context) {
        this(ShardOwners.get(context));
    }

    public ShardPagerAdapter(ShardOwner owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    @CallSuper
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Shard.State state = pageState.get(position);
        Shard shard = getItem(position);
        Page page = new Page(owner, container, shard, state);
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
    }

    @Override
    public final int getItemPosition(@NonNull Object object) {
        Page page = (Page) object;
        int position = getItemPosition(page.shard);
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
     * @param shard Shard representing an item, previously returned by a call to
     *              {@link #instantiateItem(View, int)}.
     * @return shard's new position index from [0, {@link #getCount()}),
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * or {@link #POSITION_NONE} if the item is no longer present.
     */
    public int getItemPosition(Shard shard) {
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
    public abstract Shard getItem(int position);

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((Page) object).shard.getView();
    }

    @Override
    @CallSuper
    public void restoreState(Parcelable state, @Nullable ClassLoader loader) {
        Bundle bundle = (Bundle) state;
        bundle.setClassLoader(getClass().getClassLoader());
        pageState = bundle.getSparseParcelableArray(STATE_SHARDS);
    }

    @Override
    @CallSuper
    public Parcelable saveState() {
        Bundle state = new Bundle();
        for (int i = 0; i < pages.size(); i++) {
            pageState.put(pages.keyAt(i), pages.valueAt(i).saveState());
        }
        state.putSparseParcelableArray(STATE_SHARDS, pageState);
        return state;
    }

    static class Page implements ShardOwner, LifecycleObserver {
        private final ShardOwner parentOwner;
        private final Context context;
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        private final ShardManager fm;
        final Shard shard;
        private boolean isPrimary;
        private boolean isReallyResumed;

        Page(ShardOwner parentOwner, ViewGroup container, Shard shard, @Nullable Shard.State state) {
            this.parentOwner = parentOwner;
            this.context = new ShardOwnerContextWrapper(container.getContext(), this);
            this.shard = shard;
            fm = new ShardManager(this);
            fm.restoreState(shard, state);
            fm.add(shard, container);
            parentOwner.getLifecycle().addObserver(this);
        }

        void destroy() {
            parentOwner.getLifecycle().removeObserver(this);
            fm.remove(shard);
        }

        Shard.State saveState() {
            return fm.saveState(shard);
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

        @NonNull
        @Override
        public InstanceStateStore getInstanceStateStore() {
            return parentOwner.getInstanceStateStore();
        }

        @NonNull
        @Override
        public Context getContext() {
            return context;
        }

        @NonNull
        @Override
        public Shard.Factory getShardFactory() {
            return parentOwner.getShardFactory();
        }
    }
}
