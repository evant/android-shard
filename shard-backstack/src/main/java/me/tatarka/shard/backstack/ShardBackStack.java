package me.tatarka.shard.backstack;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.transition.ShardTransition;
import me.tatarka.shard.transition.ShardTransitionCompat;

public class ShardBackStack {

    @IdRes
    public static final int NO_ID = -1;

    private static final int MSG = 0;

    private static final int OP_UNSET = 0;
    private static final int OP_PUSH = 1;
    private static final int OP_POP = 2;

    private final ShardOwner owner;
    private final FrameLayout container;
    private final ShardManager sm;

    private ArrayList<Entry> backStack = new ArrayList<>();
    private Entry currentEntry;

    private int which = 0;
    private Entry oldEntry;
    private Entry newEntry;

    public ShardBackStack(ShardOwner owner, FrameLayout container) {
        this.owner = owner;
        this.container = container;
        sm = new ShardManager(owner);
    }

    public int size() {
        return backStack.size();
    }

    public State saveState() {
        commit();
        currentEntry.save(sm);
        return new State(backStack, currentEntry);
    }

    public void restoreState(@NonNull State saveState) {
        backStack = saveState.backStack;
        currentEntry = saveState.currentEntry;
        currentEntry.restore(sm, owner.getShardFactory());
        sm.replace(null, currentEntry.shard, container);
    }

    public ShardBackStack setStarting(Shard shard) {
        return setStarting(shard, NO_ID);
    }

    public ShardBackStack setStarting(Shard shard, @IdRes int id) {
        if (currentEntry == null) {
            push(shard, id);
        }
        return this;
    }

    public ShardBackStack push(Shard shard) {
        return push(shard, NO_ID);
    }

    public ShardBackStack push(Shard shard, @IdRes int id) {
        return push(shard, id, null);
    }

    public ShardBackStack push(Shard shard, @Nullable NavOptions navOptions) {
        return push(shard, NO_ID, navOptions);
    }

    public ShardBackStack push(Shard shard, @IdRes int id, @Nullable NavOptions navOptions) {
        if (navOptions != null && navOptions.singleTop) {
            if (id == NO_ID) {
                throw new IllegalArgumentException("Must set an id when using singleTop");
            }
            if (currentEntry != null && currentEntry.id == id) {
                return this;
            }
        }

        Entry newEntry = new Entry(shard, id, navOptions);
        Entry oldEntry = currentEntry;
        currentEntry = newEntry;
        if (oldEntry != null) {
            backStack.add(oldEntry);
        }

        if (which == OP_UNSET) {
            this.oldEntry = oldEntry;
        } else if (which == OP_PUSH) {
        }

        this.which = OP_PUSH;
        this.newEntry = newEntry;
        asyncCommit();
        return this;
    }

    public ShardBackStack pop() {
        if (backStack.size() == 0) {
            return this;
        }
        Entry oldEntry = currentEntry;
        currentEntry = backStack.remove(backStack.size() - 1);

        if (which == OP_PUSH) {
            clear();
            handler.removeMessages(MSG);
            return this;
        }

        if (which == OP_UNSET) {
            this.oldEntry = oldEntry;
        } else if (which == OP_POP) {
            //?
        }

        this.which = OP_POP;
        asyncCommit();
        return this;
    }

    public ShardBackStack popToIndex(int index, boolean inclusive) {
        int size = backStack.size();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("length=" + size + " index: " + index);
        }
        int popCount = size - index + (inclusive ? 1 : 0);
        for (int i = 0; i < popCount; i++) {
            pop();
        }
        return this;
    }

    public ShardBackStack popToId(@IdRes int id, boolean inclusive) {
        if (id == NO_ID) {
            return this;
        }
        int size = backStack.size();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (backStack.get(i).id == id) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return this;
        }
        return popToIndex(index, inclusive);
    }

    public boolean commit() {
        handler.removeMessages(MSG);
        boolean willPerformAction = willPerformAction();
        doCommit();
        return willPerformAction;
    }

    void doCommit() {
        if (which == OP_PUSH) {
            for (int i = 0, size = backStack.size(); i < size; i++) {
                Entry entry = backStack.get(i);
                if (entry.isRestored()) {
                    entry.save(sm);
                }
            }

            ShardTransition transition;
            if (newEntry.transition != 0) {
                transition = ShardTransitionCompat.fromTransitionRes(container.getContext(), newEntry.transition);
            } else {
                transition = ShardTransition.fromAnimRes(container.getContext(), newEntry.enterAnim, newEntry.exitAnim);
            }
            sm.replace(oldEntry != null ? oldEntry.shard : null, newEntry.shard, container, transition);
        } else if (which == OP_POP) {
            currentEntry.restore(sm, owner.getShardFactory());
            ShardTransition transition = null;
            if (oldEntry != null) {
                if (oldEntry.transition != 0) {
                    transition = ShardTransitionCompat.fromTransitionRes(container.getContext(), oldEntry.transition);
                } else {
                    transition = ShardTransition.fromAnimRes(container.getContext(), oldEntry.popEnterAnim, oldEntry.popExitAnim);
                }
            }
            sm.replace(oldEntry != null ? oldEntry.shard : null, currentEntry.shard, container, transition);
        }
        clear();
    }

    void clear() {
        which = OP_UNSET;
        oldEntry = null;
        newEntry = null;
    }

    @SuppressLint("NewApi")
    private void asyncCommit() {
        if (!handler.hasMessages(MSG)) {
            Message message = Message.obtain();
            message.what = MSG;
            message.setAsynchronous(true);
            handler.sendMessage(message);
        }
    }

    public boolean willPerformAction() {
        return which != OP_UNSET;
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            doCommit();
            return true;
        }
    });

    static class Entry implements Parcelable {
        final String name;
        @Nullable
        Shard shard;
        @Nullable
        Shard.State state;

        final int id;
        int enterAnim;
        int exitAnim;
        int popEnterAnim;
        int popExitAnim;
        int transition;

        Entry(Shard shard, int id, @Nullable NavOptions navOptions) {
            this.name = shard.getClass().getName();
            this.shard = shard;
            this.id = id;
            if (navOptions != null) {
                enterAnim = navOptions.enterAnim;
                exitAnim = navOptions.exitAnim;
                popEnterAnim = navOptions.popEnterAnim;
                popExitAnim = navOptions.popExitAnim;
                transition = navOptions.transition;
            }
        }

        Entry(Parcel in) {
            name = in.readString();
            state = in.readParcelable(Shard.State.class.getClassLoader());
            id = in.readInt();
            popEnterAnim = in.readInt();
            popExitAnim = in.readInt();
            transition = in.readInt();
        }

        boolean isRestored() {
            return state == null;
        }

        void save(ShardManager sm) {
            if (shard == null) {
                throw new IllegalStateException("Shard isn't restored");
            }
            state = sm.saveState(shard);
        }

        void restore(ShardManager sm, Shard.Factory factory) {
            if (state == null) {
                throw new IllegalStateException("Shard isn't saved");
            }
            shard = factory.newInstance(name);
            sm.restoreState(shard, state);
            state = null;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (state == null) {
                throw new IllegalStateException("Shard isn't saved");
            }
            dest.writeString(name);
            dest.writeInt(id);
            dest.writeParcelable(state, flags);
            dest.writeInt(popEnterAnim);
            dest.writeInt(popExitAnim);
            dest.writeInt(transition);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Entry> CREATOR = new Creator<Entry>() {
            @Override
            public Entry createFromParcel(Parcel in) {
                return new Entry(in);
            }

            @Override
            public Entry[] newArray(int size) {
                return new Entry[size];
            }
        };
    }

    public static class State implements Parcelable {
        final ArrayList<Entry> backStack;
        final Entry currentEntry;

        State(ArrayList<Entry> backStack, Entry currentEntry) {
            this.backStack = backStack;
            this.currentEntry = currentEntry;
        }

        State(Parcel in) {
            backStack = in.readArrayList(getClass().getClassLoader());
            currentEntry = in.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(backStack);
            dest.writeParcelable(currentEntry, flags);
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
