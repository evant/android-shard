package me.tatarka.shard.fragment.app;

import androidx.fragment.app.Fragment;

import me.tatarka.shard.app.BaseActivityCallbacksDispatcher;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwnerDelegate;

/**
 * Helper to implement a {@link ShardFragment}
 */
public final class ShardFragmentDelegate extends ShardOwnerDelegate {

    public <F extends Fragment & ShardOwner> ShardFragmentDelegate(F fragment) {
        super(fragment, new ActivityCallbacksDispatcherFactory() {
            @Override
            public BaseActivityCallbacksDispatcher create(ShardOwner owner) {
                return new FragmentActivityCallbacksDispatcher((Fragment) owner);
            }
        });
    }
}
