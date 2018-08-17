package me.tatarka.betterfragment;

public interface FragmentFactory {
    <T extends Fragment> T newInstance(Class<T> fragmentClass);
}
