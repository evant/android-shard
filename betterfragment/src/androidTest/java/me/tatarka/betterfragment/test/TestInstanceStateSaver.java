package me.tatarka.betterfragment.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.state.InstanceStateSaver;

public class TestInstanceStateSaver implements InstanceStateSaver<Bundle> {

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
    public Bundle onSaveInstanceState() {
        saveStateCalled = true;
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, state);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle instanceState) {
        restoreStateCalled = true;
        this.state = instanceState.getInt(STATE);
    }
}
