package me.tatarka.betterfragment.nav;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.SparseArray;

import java.lang.annotation.Retention;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

import static java.lang.annotation.RetentionPolicy.SOURCE;

abstract class OptimizingNavigator<Destination extends NavDestination, Page, State extends Parcelable> extends Navigator<Destination> {

    private static final String STATE_BACK_STACK = "back_stack";
    private static final String STATE_BACK_STACK_STATE = "back_stack_state";
    private static final String STATE_CURRENT_DESTINATION = "current";
    private static final String STATE_CURRENT_ID = "currentId";
    private static final int MSG_OPS = 0;
    private final Op op = new Op();
    private final Handler handler = new Handler(op);

    private IntArrayStack backStack = new IntArrayStack();
    private SparseArray<State> backStackState = new SparseArray<>();
    private int currentId;
    @Nullable
    private Page currentPage;

    @Override
    public void navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        boolean singleTop = navOptions != null && navOptions.shouldLaunchSingleTop();

        if (singleTop && currentId == destination.getId()) {
            dispatchOnNavigatorNavigated(destination.getId(), BACK_STACK_UNCHANGED);
            return;
        }

        Page newPage = createPage(destination, args, navOptions);
        push(newPage, destination.getId());

        dispatchOnNavigatorNavigated(destination.getId(), BACK_STACK_DESTINATION_ADDED);
    }

    @Override
    public boolean popBackStack() {
        if (backStack.size() > 0) {
            pop();
            dispatchOnNavigatorNavigated(currentId, BACK_STACK_DESTINATION_POPPED);
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

    @NonNull
    protected abstract Page createPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions);

    protected abstract void replace(@Nullable Page oldPage, @NonNull Page newPage, @BackStackEffect int backStackEffect);

    @NonNull
    protected abstract State savePageState(Page page);

    @NonNull
    protected abstract Page restorePageState(State state);

    @Override
    @CallSuper
    public Bundle onSaveState() {
        Bundle state = new Bundle();
        state.putParcelable(STATE_BACK_STACK, backStack);
        state.putSparseParcelableArray(STATE_BACK_STACK_STATE, backStackState);
        if (currentPage != null) {
            state.putParcelable(STATE_CURRENT_DESTINATION, savePageState(currentPage));
            state.putInt(STATE_CURRENT_ID, currentId);
        }
        return state;
    }

    @Override
    @CallSuper
    public void onRestoreState(@NonNull Bundle savedState) {
        super.onRestoreState(savedState);
        savedState.setClassLoader(getClass().getClassLoader());
        backStack = savedState.getParcelable(STATE_BACK_STACK);
        backStackState = savedState.getSparseParcelableArray(STATE_BACK_STACK_STATE);
        State state = savedState.getParcelable(STATE_CURRENT_DESTINATION);
        currentId = savedState.getInt(STATE_CURRENT_ID);
        if (state != null) {
            currentPage = restorePageState(state);
            replace(null, currentPage, BACK_STACK_UNCHANGED);
        }
    }

    private void push(Page newPage, int newId) {
        int oldId = currentId;
        currentId = newId;
        if (oldId != 0) {
            backStack.push(oldId);
        }

        if (op.which == OP_UNSET) {
            op.oldId = oldId;
            op.oldPage = currentPage;
        } else if (op.which == OP_PUSH) {
            op.pagesToSave.put(op.newId, op.newPage);
        }
        op.which = OP_PUSH;
        op.newId = newId;
        op.newPage = newPage;
        dispatchOps();
    }

    private void pop() {
        int oldId = currentId;
        currentId = backStack.pop();

        if (op.which == OP_PUSH) {
            op.clear();
            handler.removeMessages(MSG_OPS);
            return;
        }
        if (op.which == OP_UNSET) {
            op.oldId = oldId;
            op.oldPage = currentPage;
        } else if (op.which == OP_POP) {
            backStackState.remove(op.newId);
        }
        op.which = OP_POP;
        op.newId = currentId;
        dispatchOps();
    }

    private static final int OP_UNSET = 0;
    private static final int OP_PUSH = 1;
    private static final int OP_POP = 2;

    class Op implements Handler.Callback {
        int which = 0;
        int newId;
        int oldId;
        Page oldPage;
        Page newPage;
        final SparseArray<Page> pagesToSave = new SparseArray<>();

        void clear() {
            which = OP_UNSET;
            newId = 0;
            oldId = 0;
            oldPage = null;
            newPage = null;
            pagesToSave.clear();
        }

        @Override
        public boolean handleMessage(Message msg) {
            execute();
            return true;
        }

        void execute() {
            if (which == OP_PUSH) {
                currentPage = newPage;
                if (oldPage != null) {
                    backStackState.put(oldId, savePageState(oldPage));
                }
                for (int i = 0; i < pagesToSave.size(); i++) {
                    backStackState.put(pagesToSave.keyAt(i), savePageState(pagesToSave.valueAt(i)));
                }
                replace(oldPage, newPage, BACK_STACK_DESTINATION_ADDED);
            } else if (which == OP_POP) {
                currentPage = restorePageState(backStackState.get(newId));
                backStackState.remove(newId);
                replace(oldPage, currentPage, BACK_STACK_DESTINATION_POPPED);
            }
            clear();
        }
    }

    @Retention(SOURCE)
    @IntDef({BACK_STACK_UNCHANGED, BACK_STACK_DESTINATION_ADDED, BACK_STACK_DESTINATION_POPPED})
    @interface BackStackEffect {
    }
}
