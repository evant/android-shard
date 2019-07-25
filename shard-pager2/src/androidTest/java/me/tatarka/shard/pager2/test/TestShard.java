package me.tatarka.shard.pager2.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.savedstate.SavedStateProvider;

public class TestShard extends Shard implements SavedStateProvider<Bundle> {
    private static final String STATE = "state";

    public int state;
    public boolean createCalled;
    public boolean saveInstanceStateCalled;

    @Override
    public void onCreate() {
        super.onCreate();
        createCalled = true;
        getSavedStateRegistry().registerSavedStateProvider(STATE, this);
    }

    @Nullable
    @Override
    public Bundle saveState() {
        saveInstanceStateCalled = true;
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, state);
        return bundle;
    }

    @Override
    public void restoreState(@NonNull Bundle instanceState) {
        state = instanceState.getInt(STATE);

    }
}
