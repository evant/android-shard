package me.tatarka.betterfragment;

public class DefaultRestoreStateFactory<T extends Fragment> implements Fragment.RestoreStateFactory<T> {

    public static <T extends Fragment> DefaultRestoreStateFactory<T> of(Class<T> fragmentClass) {
        return new DefaultRestoreStateFactory<>(fragmentClass);
    }

    private final Class<T> fragmentClass;

    private DefaultRestoreStateFactory(Class<T> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    @Override
    public T create() {
        try {
            return fragmentClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
