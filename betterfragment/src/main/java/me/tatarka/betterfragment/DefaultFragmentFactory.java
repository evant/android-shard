package me.tatarka.betterfragment;

public class DefaultFragmentFactory implements FragmentFactory {

    private static final FragmentFactory INSTANCE = new DefaultFragmentFactory();

    public static FragmentFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends Fragment> T newInstance(Class<T> fragmentClass) {
        return DefaultRestoreStateFactory.of(fragmentClass).create();
    }
}
