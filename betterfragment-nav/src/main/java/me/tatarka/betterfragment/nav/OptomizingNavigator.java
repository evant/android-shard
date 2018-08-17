package me.tatarka.betterfragment.nav;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.util.ArrayList;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class OptomizingNavigator<Destination extends NavDestination, Page, State extends Parcelable> extends Navigator<Destination> {

    private static final String STATE_BACK_STACK = "back_stack";
    private static final String STATE_BACK_STACK_STATE = "back_stack_state";
    private static final String STATE_CURRENT_DESTINATION = "current";
    private static final int MSG_OPS = 0;
    @Nullable
    private Op op;

    @SuppressLint("NewApi")
    private final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (op != null) {
                if (op.which == OP_PUSH) {
                    if (op.oldPage != null) {
                        backStackPush(op.id, savePageState(op.oldPage));
                    }
                    currentPage = op.newPage;
                    replace(op.oldPage, op.newPage, null, BACK_STACK_DESTINATION_ADDED);
                } else if (op.which == OP_POP) {
                    State newState = backStackPop();
                    currentPage = restorePageState(newState);
                    replace(op.oldPage, currentPage, newState, BACK_STACK_DESTINATION_POPPED);
                }
                op = null;
            }
            return true;
        }
    });

    private ArrayList<Integer> backStack = new ArrayList<>();
    private SparseArray<State> backStackState = new SparseArray<>();
    @Nullable
    private Page currentPage;

    @Override
    public void navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        push(currentPage, newPage(destination, args, navOptions), destination.getId());
        dispatchOps();
        dispatchOnNavigatorNavigated(destination.getId(), BACK_STACK_DESTINATION_ADDED);
    }

    @Override
    public boolean popBackStack() {
        if (backStack.size() > 0) {
            int id = backStackPeek();
            pop(currentPage, id);
            dispatchOps();
            dispatchOnNavigatorNavigated(id, BACK_STACK_DESTINATION_POPPED);
            return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    private void dispatchOps() {
        if (!handler.hasMessages(MSG_OPS)) {
            Message message = Message.obtain();
            message.setAsynchronous(true);
            message.what = MSG_OPS;
            handler.sendMessage(message);
        }
    }

    public abstract Page newPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions);

    public abstract void replace(@Nullable Page oldPage, @Nullable Page newPage, @Nullable State newState, @BackStackEffect int backStackEffect);

    public abstract State savePageState(Page page);

    public abstract Page restorePageState(State state);

    @Override
    @CallSuper
    public Bundle onSaveState() {
        Bundle state = new Bundle();
        state.putIntegerArrayList(STATE_BACK_STACK, backStack);
        state.putSparseParcelableArray(STATE_BACK_STACK_STATE, backStackState);
        if (currentPage != null) {
            state.putParcelable(STATE_CURRENT_DESTINATION, savePageState(currentPage));
        }
        return state;
    }

    @Override
    @CallSuper
    public void onRestoreState(@NonNull Bundle savedState) {
        super.onRestoreState(savedState);
        backStack = savedState.getIntegerArrayList(STATE_BACK_STACK);
        backStackState = savedState.getSparseParcelableArray(STATE_BACK_STACK_STATE);
        State state = savedState.getParcelable(STATE_CURRENT_DESTINATION);
        if (state != null) {
            currentPage = restorePageState(state);
            replace(null, currentPage, state, BACK_STACK_UNCHANGED);
        }
    }

    private void push(@Nullable Page oldPage, Page newPage, int id) {
        if (op == null) {
            op = new Op();
            op.id = id;
            op.which = OP_PUSH;
            op.oldPage = oldPage;
            op.newPage = newPage;
        } else if (op.which == OP_PUSH) {
            backStackPush(id, savePageState(op.newPage));
            op.id = id;
            op.newPage = newPage;
        } else if (op.which == OP_POP) {
            if (backStack.size() > 0) {
                backStackPop();
            }
            op.which = OP_PUSH;
            op.id = id;
            op.newPage = newPage;
            op.newPageState = null;
        }
    }

    private void pop(Page oldPage, int id) {
        if (op == null) {
            op = new Op();
            op.id = id;
            op.which = OP_POP;
            op.oldPage = oldPage;
        } else if (op.which == OP_POP) {
            backStackPop();
            op.id = id;
        } else if (op.which == OP_PUSH) {
            op = null;
            handler.removeMessages(MSG_OPS);
        }
    }

    private void backStackPush(int id, State state) {
        backStackState.put(id, state);
        backStack.add(id);
    }

    private State backStackPop() {
        int id = backStack.remove(backStack.size() - 1);
        State state = backStackState.get(id);
        backStackState.remove(id);
        return state;
    }

    private int backStackPeek() {
        return backStack.get(backStack.size() - 1);
    }

    static int OP_PUSH = 1;
    static int OP_POP = 2;

    class Op {
        int which;
        int id;
        @Nullable
        Page oldPage;
        @Nullable
        Page newPage;
        @Nullable
        State newPageState;
    }

    @Retention(SOURCE)
    @IntDef({BACK_STACK_UNCHANGED, BACK_STACK_DESTINATION_ADDED, BACK_STACK_DESTINATION_POPPED})
    @interface BackStackEffect {
    }
}
