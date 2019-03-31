package me.tatarka.shard.fragment.app;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.tatarka.shard.app.ActivityCallbacksDispatcher;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwnerContextWrapper;
import me.tatarka.shard.app.ShardOwnerDelegate;

/**
 * Helper to implement a {@link ShardFragment}.
 */
public final class ShardFragmentDelegate extends ShardOwnerDelegate {

    private final Fragment fragment;

    @Nullable
    private Context context;

    public <F extends Fragment & ShardOwner> ShardFragmentDelegate(F fragment) {
        super(fragment, new ActivityCallbacksDispatcherFactory() {
            @Override
            public ActivityCallbacksDispatcher create(ShardOwner owner) {
                return new ActivityCallbacksFragmentDispatcher((Fragment) owner);
            }
        });
        this.fragment = fragment;
    }

    /**
     * Wraps the given context so that a {@link ShardOwner} can be obtained from it.
     */
    @Nullable
    public Context wrapContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        if (this.context == null) {
            this.context = new ShardOwnerContextWrapper(context, fragment);
        }
        return this.context;
    }

    /**
     * Wraps the given layout inflater so that a {@link ShardOwner} can be obtained from it.
     */
    public LayoutInflater wrapLayoutInflater(LayoutInflater inflater) {
        return inflater.cloneInContext(fragment.getContext());
    }
}
