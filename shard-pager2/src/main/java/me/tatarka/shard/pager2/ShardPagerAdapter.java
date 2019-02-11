package me.tatarka.shard.pager2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.savedstate.SavedStateRegistry;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.StatefulAdapter;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwnerContextWrapper;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.content.ComponentCallbacks;

/**
 * Implementation of {@link PagerAdapter} that represents each page as a {@link Shard}.
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getItemCount()} to have a working adapter.
 *
 * <p>
 * {@link ViewPager#getOffscreenPageLimit()} number of shards will be kept in memory. Otherwise
 * their state will be saved and the shard destroyed. The current shard will be in the resumed
 * state and all other shards will be in the started state. You must override
 * {@link #getItemId(int)} and {@link #containsItem(long)} if you want to be able to dynamically
 * change the contents on a {@link #notifyDataSetChanged()}.
 */
public abstract class ShardPagerAdapter extends RecyclerView.Adapter<ShardViewHolder> implements StatefulAdapter {

    private final ShardOwner owner;
    private long primaryId = -1;
    private final LongSparseArray<Page> pages = new LongSparseArray<>();
    private final LongSparseArray<Shard.State> pageState = new LongSparseArray<>();
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int primaryPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
                long primaryId = -1;
                if (primaryPosition != RecyclerView.NO_POSITION) {
                    primaryId = recyclerView.findViewHolderForLayoutPosition(primaryPosition).getItemId();
                }
                updatePrimaryPage(primaryId);
            }
        }
    };

    public ShardPagerAdapter(Context context) {
        this(ShardOwners.get(context));
    }

    public ShardPagerAdapter(ShardOwner owner) {
        this.owner = owner;
        super.setHasStableIds(true);
    }

    @Override
    public final void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    public final void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.removeOnScrollListener(onScrollListener);
    }

    @NonNull
    public abstract Shard getItem(int position);

    @NonNull
    @Override
    public final ShardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ShardViewHolder.create(parent);
    }

    @Override
    public final void onBindViewHolder(@NonNull final ShardViewHolder holder, int position) {
        Shard shard = getItem(position);
        long itemId = getItemId(position);
        holder.page = new Page(owner, shard, pageState.get(itemId));
        holder.page.setPrimary(itemId == primaryId);
        pages.put(itemId, holder.page);

        /** Special case when {@link RecyclerView} decides to keep the {@link container}
         * attached to the window, but not to the view hierarchy (i.e. parent is null) */
        final ViewGroup container = holder.getContainer();
        if (ViewCompat.isAttachedToWindow(container)) {
            if (container.getParent() != null) {
                throw new IllegalStateException("Design assumption violated.");
            }
            container.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (container.getParent() != null) {
                        container.removeOnLayoutChangeListener(this);
                        onViewAttachedToWindow(holder);
                    }
                }
            });
        }
    }

    @Override
    public final void onViewAttachedToWindow(@NonNull ShardViewHolder holder) {
        if (holder.page.isAdded) {
            return;
        }
        holder.page.add(holder.getContainer());
    }

    @Override
    public void onViewRecycled(@NonNull ShardViewHolder holder) {
        removeShard(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ShardViewHolder holder) {
        // This happens when a ViewHolder is in a transient state (e.g. during custom
        // animation). We don't have sufficient information on how to clear up what lead to
        // the transient state, so we are throwing away the ViewHolder to stay on the
        // conservative side.
        removeShard(holder);
        return false; // don't recycle the view
    }

    private void removeShard(ShardViewHolder holder) {
        long itemId = holder.getItemId();
        if (containsItem(itemId)) {
            pageState.put(itemId, holder.page.saveState());
        }
        holder.page.destroy();
        holder.page = null;
        pages.remove(itemId);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < getItemCount();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        throw new UnsupportedOperationException(
                "Stable Ids are required for the adapter to function properly, and the adapter "
                        + "takes care of setting the flag.");
    }

    @Override
    public void restoreState(@NonNull Parcelable savedState) {
        State state = (State) savedState;
        primaryId = state.primaryId;
        state.restore(pageState);
    }

    @NonNull
    @Override
    @CallSuper
    public Parcelable saveState() {
        for (int i = 0; i < pages.size(); i++) {
            pageState.put(pages.keyAt(i), pages.valueAt(i).saveState());
        }
        return new State(primaryId, pageState);
    }

    private void updatePrimaryPage(long primaryId) {
        for (int i = 0; i < pages.size(); i++) {
            long itemId = pages.keyAt(i);
            Page page = pages.valueAt(i);
            page.setPrimary(primaryId == itemId);
        }
    }

    static class Page implements ShardOwner, LifecycleEventObserver {
        private final ShardOwner parentOwner;
        private final Context context;
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
        private final ShardManager fm;
        final Shard shard;
        private boolean isPrimary;
        private boolean isReallyResumed;
        boolean isAdded;

        Page(ShardOwner parentOwner, Shard shard, @Nullable Shard.State state) {
            this.parentOwner = parentOwner;
            this.context = new ShardOwnerContextWrapper(parentOwner.getContext(), this);
            this.shard = shard;
            fm = new ShardManager(this);
            fm.restoreState(shard, state);
        }

        void add(ViewGroup container) {
            isAdded = true;
            fm.add(shard, container);
            parentOwner.getLifecycle().addObserver(this);
        }

        void destroy() {
            isAdded = false;
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
                    lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
                } else {
                    lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
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

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_RESUME) {
                isReallyResumed = true;
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                isReallyResumed = false;
            }
            if (isPrimary || (event != Lifecycle.Event.ON_PAUSE && event != Lifecycle.Event.ON_RESUME)) {
                lifecycleRegistry.handleLifecycleEvent(event);
            }
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

        @NonNull
        @Override
        public ActivityCallbacks getActivityCallbacks() {
            return parentOwner.getActivityCallbacks();
        }

        @NonNull
        @Override
        public ComponentCallbacks getComponentCallbacks() {
            return parentOwner.getComponentCallbacks();
        }

        @NonNull
        @Override
        public OnBackPressedDispatcher getOnBackPressedDispatcher() {
            return parentOwner.getOnBackPressedDispatcher();
        }

        @NonNull
        @Override
        public SavedStateRegistry getSavedStateRegistry() {
            return parentOwner.getSavedStateRegistry();
        }
    }

    static final class State implements Parcelable {
        final long primaryId;
        final long[] keys;
        final Shard.State[] values;

        State(long primaryId, LongSparseArray<Shard.State> states) {
            this.primaryId = primaryId;
            int length = states.size();
            keys = new long[length];
            values = new Shard.State[length];
            for (int i = 0; i < length; i++) {
                long itemId = states.keyAt(i);
                keys[i] = itemId;
                values[i] = states.get(itemId);
            }
        }

        State(Parcel in) {
            primaryId = in.readLong();
            keys = in.createLongArray();
            values = in.createTypedArray(Shard.State.CREATOR);
        }

        void restore(LongSparseArray<Shard.State> states) {
            states.clear();
            for (int i = 0; i < keys.length; i++) {
                long itemId = keys[i];
                states.put(itemId, values[i]);
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(primaryId);
            dest.writeLongArray(keys);
            dest.writeTypedArray(values, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
}
