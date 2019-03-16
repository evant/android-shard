package me.tatarka.shard.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

public class TestInstanceStateSaver {

    private static final String STATE = "state";

    public int state;
    public boolean saveStateCalled;
    public boolean restoreStateCalled;

    public TestInstanceStateSaver(String key, SavedStateRegistryOwner owner) {
        Bundle bundle = owner.getSavedStateRegistry().consumeRestoredStateForKey(key);
        if (bundle != null) {
            state = bundle.getInt(STATE);
            restoreStateCalled = true;
        }
        owner.getSavedStateRegistry().registerSavedStateProvider(key, new SavedStateRegistry.SavedStateProvider() {
            @NonNull
            @Override
            public Bundle saveState() {
                saveStateCalled = true;
                Bundle bundle = new Bundle();
                bundle.putInt(STATE, state);
                return bundle;
            }
        });
    }

    public TestInstanceStateSaver(String key, int initialState, SavedStateRegistryOwner owner) {
        this(key, owner);
        this.state = initialState;
    }
}
