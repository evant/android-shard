package me.tatarka.shard.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.savedstate.SavedStateProvider;

public class TestInstanceStateSaver implements SavedStateProvider<Bundle> {

    private static final String STATE = "state";

    public int state;
    public boolean saveStateCalled;
    public boolean restoreStateCalled;

    public TestInstanceStateSaver() {
    }

    public TestInstanceStateSaver(int state) {
        this.state = state;
    }

    @Nullable
    @Override
    public Bundle saveState() {
        saveStateCalled = true;
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, state);
        return bundle;
    }

    @Override
    public void restoreState(@NonNull Bundle instanceState) {
        restoreStateCalled = true;
        this.state = instanceState.getInt(STATE);
    }
}
