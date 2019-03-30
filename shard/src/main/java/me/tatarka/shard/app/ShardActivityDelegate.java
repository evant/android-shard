package me.tatarka.shard.app;

import androidx.activity.ComponentActivity;

/**
 * Helper to implement a {@link ShardActivity}
 */
public final class ShardActivityDelegate extends ShardOwnerDelegate {

    public <A extends ComponentActivity & ShardOwner> ShardActivityDelegate(A activity) {
        super(activity, new ActivityCallbacksDispatcherFactory() {
            @Override
            public ActivityCallbacksActivityDispatcher create(ShardOwner owner) {
                return new ActivityCallbacksActivityDispatcher((ComponentActivity) owner);
            }
        });
    }
}
