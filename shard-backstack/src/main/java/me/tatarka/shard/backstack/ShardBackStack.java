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

/**
 * A simple back stack implementation for shards.
 */
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

    /**
     * Constructs a new back stack that puts shards in the given container. You probably want to use
     * {@link ShardBackStackHost} instead of constructing this directly as it will handle saving
     * and restoring state as well as the system back button.
     */
    public ShardBackStack(ShardOwner owner, FrameLayout container) {
        this.owner = owner;
        this.container = container;
        sm = new ShardManager(owner);
    }

    /**
     * Returns the number of entries in the back stack.
     */
    public int size() {
        return backStack.size();
    }

    /**
     * Saves the back stacks's state and returns it.
     */
    public State saveState() {
        commit();
        currentEntry.save(sm);
        return new State(backStack, currentEntry);
    }

    /**
     * Restores the back stack from the given state.
     */
    public void restoreState(@NonNull State saveState) {
        backStack = saveState.backStack;
        currentEntry = saveState.currentEntry;
        currentEntry.restore(sm, owner.getShardFactory());
        sm.replace(null, currentEntry.shard, container);
    }

    /**
     * Sets the starting shard for the back stack. You will not be able to pop this shard. This is
     * an asynchronous operation to allow optimizations when you make multiple back stack operations.
     * If you need it to happen immediately you can call {@link #commit()}.
     */
    public ShardBackStack setStarting(Shard shard) {
        return setStarting(shard, NO_ID);
    }

    /**
     * Sets the starting shard for the back stack. You will not be able to pop this shard. This is
     * an asynchronous operation to allow optimizations when you make multiple back stack operations.
     * If you need it to happen immediately you can call {@link #commit()}.
     *
     * @param id A unique id for the shard for use with {@link #popToId(int, boolean)}.
     */
    public ShardBackStack setStarting(Shard shard, @IdRes int id) {
        if (currentEntry == null) {
            push(shard, id, false);
        }
        return this;
    }

    /**
     * Pushes the given shard onto the back stack. This is an asynchronous operation to allow
     * optimizations when you make multiple back stack operations. If you need it to happen
     * immediately you can call {@link #commit()}.
     */
    public ShardBackStack push(Shard shard) {
        return push(shard, NO_ID, false);
    }

    /**
     * Pushes the given shard onto the back stack. This is an asynchronous operation to allow
     * optimizations when you make multiple back stack operations. If you need it to happen
     * immediately you can call {@link #commit()}.
     *
     * @param id A unique id for the shard for use with {@link #popToId(int, boolean)}.
     */
    public ShardBackStack push(Shard shard, @IdRes int id) {
        return push(shard, id, false);
    }

    /**
     * Pushes the given shard onto the back stack. This is an asynchronous operation to allow
     * optimizations when you make multiple back stack operations. If you need it to happen
     * immediately you can call {@link #commit()}.
     *
     * @param id        A unique id for the shard for use with {@link #popToId(int, boolean)}.
     * @param singleTop If true and a shard with the same id is on the top of the back stack, this
     *                  push will do nothing.
     */
    public ShardBackStack push(Shard shard, @IdRes int id, boolean singleTop) {
        return push(shard, id, singleTop, null);
    }

    /**
     * Pushes the given shard onto the back stack. This is an asynchronous operation to allow
     * optimizations when you make multiple back stack operations. If you need it to happen
     * immediately you can call {@link #commit()}.
     *
     * @param navShardTransition Additional navigation options to control ex: animations.
     */
    public ShardBackStack push(Shard shard, @Nullable NavShardTransition navShardTransition) {
        return push(shard, NO_ID, false, navShardTransition);
    }

    /**
     * Pushes the given shard onto the back stack. This is an asynchronous operation to allow
     * optimizations when you make multiple back stack operations. If you need it to happen
     * immediately you can call {@link #commit()}.
     *
     * @param id                 A unique id for the shard for use with {@link #popToId(int, boolean)}.
     * @param singleTop          If true and a shard with the same id is on the top of the back stack, this
     *                           push will do nothing.
     * @param navShardTransition Transition animations to run when pushing and popping the shard.
     */
    public ShardBackStack push(Shard shard, @IdRes int id, boolean singleTop, @Nullable NavShardTransition navShardTransition) {
        if (singleTop) {
            if (id == NO_ID) {
                throw new IllegalArgumentException("Must set an id when using singleTop");
            }
            if (currentEntry != null && currentEntry.id == id) {
                return this;
            }
        }

        Entry newEntry = new Entry(shard, id, navShardTransition);
        Entry oldEntry = currentEntry;
        currentEntry = newEntry;
        if (oldEntry != null) {
            backStack.add(oldEntry);
        }

        if (which == OP_UNSET) {
            this.oldEntry = oldEntry;
        }

        this.which = OP_PUSH;
        this.newEntry = newEntry;
        asyncCommit();
        return this;
    }

    /**
     * Pops the top shard off the back stack if possible, otherwise does nothing. This is an
     * asynchronous operation to allow optimizations when you make multiple back stack operations.
     * If you need it to happen immediately you can call {@link #commit()}.
     */
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
        }

        this.which = OP_POP;
        asyncCommit();
        return this;
    }

    /**
     * Pops shards off the back stack until the given index, where 0 is the starting shard, 1 is the
     * next shard up, etc.
     *
     * @param inclusive If true then the shard at the given index will be popped, otherwise it will
     *                  not.
     * @throws IndexOutOfBoundsException If the index is negative or larger than the back stack size.
     */
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

    /**
     * Pops shards off the back stack until the given id. If no entries have the given id then
     * nothing will happen.
     *
     * @param inclusive If true then the Shard at the given index will be popped, otherwise it will
     *                  not.
     */
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

    /**
     * Immediately runs all pending shard actions. While you don't normally need to call this, it
     * can be useful for when you need to ensure the latest shard is showing.
     *
     * @return true if actions were run, false otherwise.
     */
    public boolean commit() {
        handler.removeMessages(MSG);
        boolean willPerformAction = willPerformAction();
        doCommit();
        return willPerformAction;
    }

    /**
     * Returns true if there are any pending shard actions to run. This can be useful to know if a
     * previous push or pop will actually do anything.
     */
    public boolean willPerformAction() {
        return which != OP_UNSET;
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

        Entry(Shard shard, int id, @Nullable NavShardTransition navShardTransition) {
            this.name = shard.getClass().getName();
            this.shard = shard;
            this.id = id;
            if (navShardTransition != null) {
                enterAnim = navShardTransition.enterAnim;
                exitAnim = navShardTransition.exitAnim;
                popEnterAnim = navShardTransition.popEnterAnim;
                popExitAnim = navShardTransition.popExitAnim;
                transition = navShardTransition.transition;
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
