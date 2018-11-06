package me.tatarka.shard.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.R;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.transition.ShardTransition;

public class ShardPageHost extends FrameLayout {

    private ShardOwner owner;
    private ShardManager fm;
    @Nullable
    private Adapter adapter;
    @Nullable
    private OnPageChangedListener listener;
    @Nullable
    private Shard shard;
    @IdRes
    private int startPage;
    @IdRes
    private int currentPage;
    private SparseArray<Shard.State> shardStates = new SparseArray<>();
    @Nullable
    private ShardTransition defaultTransition;

    public ShardPageHost(@NonNull Context context) {
        this(context, null);
    }

    public ShardPageHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            owner = ShardOwners.get(context);
            fm = new ShardManager(owner);
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShardPageHost);
            startPage = a.getResourceId(R.styleable.ShardPageHost_startPage, startPage);
            int transitionId = a.getResourceId(R.styleable.ShardPageHost_transition, 0);
            if (transitionId != 0) {
                defaultTransition = ShardTransition.fromTransitionRes(context, transitionId);
            } else {
                int enterAnimId = a.getResourceId(R.styleable.ShardPageHost_enterAnim, 0);
                int exitAnimId = a.getResourceId(R.styleable.ShardPageHost_exitAnim, 0);
                if (enterAnimId != 0 || exitAnimId != 0) {
                    defaultTransition = ShardTransition.fromAnimRes(context, enterAnimId, exitAnimId);
                }
            }
            a.recycle();
        }
        if (!isInEditMode()) {
            if (startPage != 0 && !owner.getInstanceStateStore().isStateRestored()) {
                setCurrentPage(startPage, null);
            }
        }
    }

    public void setAdapter(@Nullable Adapter adapter) {
        if (this.adapter == adapter) {
            return;
        }
        this.adapter = adapter;
        shardStates.clear();
        if (adapter != null && currentPage != 0) {
            setShard(0, currentPage, adapter.newInstance(currentPage), defaultTransition);
        }
    }

    public void setDefaultTransition(@Nullable ShardTransition transition) {
        defaultTransition = transition;
    }

    @Nullable
    public ShardTransition getDefaultTransition() {
        return defaultTransition;
    }

    private void setShard(@IdRes int oldId, @IdRes int newId, @Nullable Shard shard, @Nullable ShardTransition transition) {
        Shard oldShard = this.shard;
        this.shard = shard;
        if (oldShard != null && oldId != 0) {
            shardStates.put(oldId, fm.saveState(oldShard));
        }
        if (shard != null && newId != 0) {
            fm.restoreState(shard, shardStates.get(newId));
        }
        fm.replace(oldShard, shard, this, transition);
        if (listener != null) {
            listener.onPageChanged(newId);
        }
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setCurrentPage(@IdRes int id) {
        setCurrentPage(id, null);
    }

    public final void setCurrentPage(@IdRes int id, @Nullable ShardTransition transition) {
        if (currentPage == id) {
            return;
        }
        int oldId = currentPage;
        currentPage = id;
        if (adapter != null) {
            setShard(oldId, id, adapter.newInstance(id), transition != null ? transition : defaultTransition);
        }
    }

    @IdRes
    public int getCurrentPage() {
        return currentPage;
    }

    @Nullable
    public Shard getShard() {
        return shard;
    }

    public void setOnPageChangedListener(@Nullable OnPageChangedListener listener) {
        this.listener = listener;
        if (listener != null && currentPage != 0) {
            listener.onPageChanged(currentPage);
        }
    }

    public interface Adapter {
        @Nullable
        Shard newInstance(@IdRes int id);
    }

    public interface OnPageChangedListener {
        void onPageChanged(@IdRes int id);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (shard != null) {
            shardStates.put(currentPage, fm.saveState(shard));
        }
        return new SavedState(super.onSaveInstanceState(), currentPage, shardStates);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPage = savedState.currentItem;
        shardStates = savedState.shardStates;

        if (adapter != null && currentPage != 0) {
            setShard(0, currentPage, adapter.newInstance(currentPage), null);
        }
    }

    public static class SavedState extends BaseSavedState {
        final int currentItem;
        @Nullable
        final SparseArray shardStates;

        SavedState(Parcelable superState, int currentItem, SparseArray shardStates) {
            super(superState);
            this.currentItem = currentItem;
            this.shardStates = shardStates;
        }

        SavedState(Parcel source) {
            super(source);
            currentItem = source.readInt();
            shardStates = source.readSparseArray(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentItem);
            out.writeSparseArray(shardStates);
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
