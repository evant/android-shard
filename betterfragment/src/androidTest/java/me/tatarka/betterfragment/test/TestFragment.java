package me.tatarka.betterfragment.test;

import android.view.View;

import me.tatarka.betterfragment.app.Fragment;

public class TestFragment extends Fragment {
    public boolean createCalled;

    @Override
    public void onCreate() {
        createCalled = true;
        setContentView(new View(getContext()));
    }
}
