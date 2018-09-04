package me.tatarka.betterfragment.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.state.StateSaver;

public class TestStateSaver implements StateSaver {

    private static final String STATE = "state";

    public int state;
    public boolean saveStateCalled;
    public boolean restoreStateCalled;

    public TestStateSaver() {
    }

    public TestStateSaver(int state) {
        this.state = state;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        saveStateCalled = true;
        outState.putInt(STATE, state);
    }

    @Override
    public void onRestoreState(@NonNull Bundle instanceState) {
        restoreStateCalled = true;
        state = instanceState.getInt(STATE);
    }
}
