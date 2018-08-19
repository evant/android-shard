package me.tatarka.betterfragment;

import androidx.annotation.NonNull;

public class DefaultFragmentFactory implements Fragment.Factory {

    private static final Fragment.Factory INSTANCE = new DefaultFragmentFactory();

    @NonNull
    public static Fragment.Factory getInstance() {
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends Fragment> T newInstance(@NonNull Class<T> fragmentClass) {
        return Fragment.newInstance(fragmentClass);
    }
}
