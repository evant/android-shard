package me.tatarka.shard.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.state.InstanceStateRegistry;
import me.tatarka.shard.transition.ShardTransition;

public final class ShardManager {

    private final ShardOwner owner;

    public ShardManager(@NonNull ShardOwner owner) {
        this.owner = owner;
    }

    /**
     * Adds the shard, attaching is to the given container and calling {@link Shard#onCreate()}.
     * This method is a no-op if the shard is already destroyed.
     *
     * @throws IllegalStateException If the shard has already been created or has been created.
     */
    public void add(@NonNull Shard shard, @NonNull ViewGroup container) {
        shard.add(owner, new ViewGroupContainer(container));
    }

    public void add(@NonNull Shard shard, @NonNull Shard.Container container) {
        shard.add(owner, container);
    }

    /**
     * Restores the shard's state. This must be called <em>before</em> the shard is added.
     *
     * @throws IllegalStateException    If the shard has already been created.
     * @throws IllegalArgumentException If the state is not for this shard.
     */
    public void restoreState(@NonNull Shard shard, @Nullable Shard.State state) {
        shard.restoreState(state);
    }

    /**
     * Saves the shard's state and returns it. This will move the shard to the stopped state
     * so that when {@link InstanceStateRegistry#onSaveInstanceState()} is called is consistent. Therefore you
     * should only call this method when the shard is being stopped or destroyed.
     *
     * @throws IllegalStateException If the shard is destroyed.
     */
    @NonNull
    public Shard.State saveState(Shard shard) {
        return shard.saveInstanceState();
    }

    /**
     * Removes the shard. After this most operations on this instance will throw an exception.
     * Note: you should not call this on configuration changes, only when you are actually done with
     * it.
     *
     * @throws IllegalStateException If the shard is already destroyed.
     */
    public void remove(Shard shard) {
        shard.remove();
    }

    /**
     * Replaces the shard.
     *
     * @param oldShard  The shard to remove, if present.
     * @param newShard  The shard to add, if present.
     * @param container The container hosting the shards.
     */
    public void replace(@Nullable Shard oldShard,
                        @Nullable Shard newShard,
                        @NonNull FrameLayout container) {
        replace(oldShard, newShard, container, null);
    }

    /**
     * Replaces the shard.
     *
     * @param oldShard   The shard to remove, if present.
     * @param newShard   The shard to add, if present.
     * @param container  The container hosting the shards.
     * @param transition An optional transition to animate between shards.
     */
    public void replace(@Nullable Shard oldShard,
                        @Nullable Shard newShard,
                        @NonNull FrameLayout container,
                        @Nullable ShardTransition transition) {
        if (!container.isLaidOut()) {
            transition = null;
        }
        if (oldShard != null) {
            ViewGroup oldView = oldShard.getView();
            if (transition != null && oldView != null) {
                transition.captureBefore(oldView);
            }
            oldShard.remove();
        }
        if (newShard != null) {
            newShard.add(owner, new ViewGroupContainer(container));
            ViewGroup newView = newShard.getView();
            if (transition != null && newView != null) {
                transition.captureAfter(newView);
            }
            if (transition != null) {
                transition.start();
            }
        }
    }

    static class ViewGroupContainer implements Shard.Container {
        private final ViewGroup frame;

        ViewGroupContainer(ViewGroup frame) {
            this.frame = frame;
        }

        @Override
        public void addView(View view) {
            frame.addView(view);
        }

        @Override
        public void removeView(View view) {
            frame.removeView(view);
        }
    }
}
