package me.tatarka.shard.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import me.tatarka.shard.savedstate.SavedStateProvider;

/**
 * Responsible for showing {@link DialogShard}s and {@link AlertDialogShard}s, and restoring them
 * across configuration changes.
 */
public class ShardDialogHost {

    private static final String DIALOG_STATE = "me.tatarka.shard.widget.ShardDialogHost";

    private final ShardOwner owner;
    private final ShardManager fm;
    private final ArrayList<BaseDialogShard> dialogShards = new ArrayList<>();

    /**
     * Constructs a new instance from the given {@link Context}. A {@link ShardOwner} must be able
     * to be obtained from the context.
     */
    public ShardDialogHost(Context context) {
        this(ShardOwners.get(context));
    }

    /**
     * Constructs a new instance from the given {@link ShardOwner}.
     */
    public ShardDialogHost(ShardOwner owner) {
        this.owner = owner;
        fm = new ShardManager(owner);
        DialogHostCallbacks callbacks = new DialogHostCallbacks();
        owner.getShardSavedStateRegistry().registerSavedStateProvider(DIALOG_STATE, callbacks);
        owner.getLifecycle().addObserver(callbacks);
    }

    @NonNull
    public Shard.Factory getShardFactory() {
        return owner.getShardFactory();
    }

    /**
     * Shows the given {@link DialogShard} or {@link AlertDialogShard}.
     */
    public void show(BaseDialogShard shard) {
        dialogShards.add(shard);
        doShow(shard);
    }

    void doShow(final BaseDialogShard shard) {
        if (shard.isShowing()) {
            return;
        }
        Dialog dialog = shard.createDialog(fm, owner.getContext());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shard.onDismiss(dialog);
                dialogShards.remove(shard);
                fm.remove(shard);
            }
        });
        dialog.setOnCancelListener(shard);
        dialog.show();
    }

    class DialogHostCallbacks implements SavedStateProvider<State>, LifecycleObserver {

        @Nullable
        @Override
        public State saveState() {
            int size = dialogShards.size();
            if (size == 0) {
                return null;
            }
            ArrayMap<String, Shard.State> states = new ArrayMap<>(size);
            for (int i = 0; i < size; i++) {
                Shard shard = dialogShards.get(i);
                states.put(shard.getClass().getName(), fm.saveState(shard));
            }
            return new State(states);
        }

        @Override
        public void restoreState(@NonNull State instanceState) {
            ArrayMap<String, Shard.State> states = instanceState.shardStates;
            for (int i = 0, size = states.size(); i < size; i++) {
                String name = states.keyAt(i);
                Shard.State shardState = states.valueAt(i);
                BaseDialogShard shard = getShardFactory().newInstance(name, shardState.getArgs());
                fm.restoreState(shard, shardState);
                dialogShards.add(shard);
                if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    doShow(shard);
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        void onCreate() {
            for (BaseDialogShard shard : dialogShards) {
                doShow(shard);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            for (BaseDialogShard shard : dialogShards) {
                shard.destroyDialog();
            }
        }
    }

    public static class State implements Parcelable {
        final ArrayMap<String, Shard.State> shardStates;

        State(ArrayMap<String, Shard.State> shardStates) {
            this.shardStates = shardStates;
        }

        State(Parcel in) {
            shardStates = new ArrayMap<>();
            in.readMap(shardStates, getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeMap(shardStates);
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
