package me.tatarka.shard.pager.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.savedstate.SavedStateRegistry;

import me.tatarka.shard.app.Shard;

public class TestShard extends Shard {
    private static final String STATE = "state";

    public int state;
    public boolean createCalled;
    public boolean saveInstanceStateCalled;

    @Override
    public void onCreate() {
        super.onCreate();
        createCalled = true;
        SavedStateRegistry registry = getSavedStateRegistry();
        Bundle bundle = registry.consumeRestoredStateForKey(STATE);
        if (bundle != null) {
            state = bundle.getInt(STATE);
        }
        registry.registerSavedStateProvider(STATE, new SavedStateRegistry.SavedStateProvider() {
            @NonNull
            @Override
            public Bundle saveState() {
                saveInstanceStateCalled = true;
                Bundle bundle = new Bundle();
                bundle.putInt(STATE, state);
                return bundle;
            }
        });
    }
}
