package me.tatarka.betterfragment.test.widget;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.Fragment;

public class TestFragment extends Fragment {
    private static final String STATE = "state";

    public int state;
    public boolean createCalled;
    public boolean saveInstanceStateCalled;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        createCalled = true;
        if (savedState != null) {
            state = savedState.getInt(STATE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceStateCalled = true;
        outState.putInt(STATE, state);
    }
}