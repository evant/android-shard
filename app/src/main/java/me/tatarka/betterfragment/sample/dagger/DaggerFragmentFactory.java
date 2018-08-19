package me.tatarka.betterfragment.sample.dagger;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.Fragment;

@Singleton
public class DaggerFragmentFactory implements Fragment.Factory {

    private final Map<Class<? extends Fragment>, Provider<Fragment>> fragmentMap;

    @Inject
    public DaggerFragmentFactory(Map<Class<? extends Fragment>, Provider<Fragment>> fragmentMap) {
        this.fragmentMap = fragmentMap;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Fragment> T newInstance(@NonNull Class<T> fragmentClass) {
        return (T) fragmentMap.get(fragmentClass).get();
    }
}
