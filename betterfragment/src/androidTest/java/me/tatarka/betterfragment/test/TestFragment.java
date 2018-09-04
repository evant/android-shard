package me.tatarka.betterfragment.test;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.state.StateSaver;

public class TestFragment extends Fragment implements StateSaver {
    private static final String STATE = "state";

    public int state;
    public boolean createCalled;
    public boolean saveInstanceStateCalled;

    @Override
    public void onRestoreState(@NonNull Bundle instanceState) {
        state = instanceState.getInt(STATE);
    }

    @Override
    public void onCreate() {
        createCalled = true;
        setContentView(new View(getContext()));
        getStateStore().addStateSaver(STATE, this);
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        saveInstanceStateCalled = true;
        outState.putInt(STATE, state);
    }
}
