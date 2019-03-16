package me.tatarka.shard.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.savedstate.SavedStateRegistry;

import java.util.WeakHashMap;

final class ShardUtil {

    static final String KEY = "me.tatarka.shard.ShardUtil";
    static final WeakHashMap<ShardOwner, Boolean> restoredMap = new WeakHashMap<>();

    static boolean isRestoringState(ShardOwner owner) {
        SavedStateRegistry registry = owner.getSavedStateRegistry();
        if (!registry.isRestored()) {
            return false;
        }
        Boolean restored = restoredMap.get(owner);
        if (restored != null) {
            return restored;
        }
        // We just need a maker so we can check if we have any saved state.
        registry.registerSavedStateProvider(KEY, new SavedStateRegistry.SavedStateProvider() {
            @NonNull
            @Override
            public Bundle saveState() {
                return Bundle.EMPTY;
            }
        });
        boolean result = registry.consumeRestoredStateForKey(KEY) != null;
        restoredMap.put(owner, result);
        return result;
    }
}
