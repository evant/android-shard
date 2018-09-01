package me.tatarka.betterfragment.test.widget;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.Fragment;

public class TestFragmentFactory implements Fragment.Factory {

    public boolean newInstanceCalled;

    @NonNull
    @Override
    public <T extends Fragment> T newInstance(@NonNull Class<T> fragmentClass) {
        newInstanceCalled = true;
        return (T) new TestFragment();
    }
}
