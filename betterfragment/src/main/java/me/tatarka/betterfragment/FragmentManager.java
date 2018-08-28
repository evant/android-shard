package me.tatarka.betterfragment;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FragmentManager {

    private final FragmentOwner owner;

    public FragmentManager(@NonNull FragmentOwner owner) {
        this.owner = owner;
    }

    /**
     * Adds the fragment, attaching is to the given container and calling {@link Fragment#onCreate(Bundle)}.
     * This method is a no-op if the fragment is already destroyed.
     */
    public void add(@NonNull Fragment fragment, @NonNull ViewGroup container) {
        add(fragment, container, null);
    }

    /**
     * Adds the fragment, attaching is to the given container and calling {@link Fragment#onCreate(Bundle)}.
     * If state is not null, it will restore itself from the given state. This method is a no-op if
     * the fragment is already destroyed.
     *
     * @throws IllegalStateException    If the fragment has already been created or has been created.
     * @throws IllegalArgumentException If the state is not for this fragment.
     */
    public void add(@NonNull Fragment fragment, @NonNull ViewGroup container, @Nullable Fragment.State state) {
        fragment.add(owner, container, state);
    }

    /**
     * Saves the fragment's state and returns it. This will move the fragment to the stopped state
     * so that when {@link Fragment#onSaveInstanceState(Bundle)} is called is consistent. Therefore you
     * should only call this method when the fragment is being stopped or destroyed.
     *
     * @throws IllegalStateException If the fragment is destroyed.
     */
    @NonNull
    public Fragment.State saveState(Fragment fragment) {
        return fragment.saveState();
    }

    /**
     * Removes the fragment. After this most operations on this instance will throw an exception.
     * Note: you should not call this on configuration changes, only when you are actually done with
     * it.
     *
     * @throws IllegalStateException If the fragment is already destroyed.
     */
    public void remove(Fragment fragment) {
        fragment.remove();
    }
}
