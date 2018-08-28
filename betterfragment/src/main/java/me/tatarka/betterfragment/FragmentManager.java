package me.tatarka.betterfragment;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class FragmentManager {

    private final FragmentOwner owner;

    public FragmentManager(@NonNull FragmentOwner owner) {
        this.owner = owner;
    }

    /**
     * Adds the fragment, attaching is to the given container and calling {@link Fragment#onCreate(Bundle)}.
     * This method is a no-op if the fragment is already destroyed.
     *
     * @throws IllegalStateException If the fragment has already been created or has been created.
     */
    public void add(@NonNull Fragment fragment, @NonNull ViewGroup container) {
        fragment.add(owner, container);
    }

    /**
     * Restores the fragment's state. This must be called <em>before</em> the fragment is added.
     *
     * @throws IllegalStateException    If the fragment has already been created.
     * @throws IllegalArgumentException If the state is not for this fragment.
     */
    public void restoreState(@NonNull Fragment fragment, @Nullable Fragment.State state) {
        fragment.restoreState(state);
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

    /**
     * Replaces the fragment.
     *
     * @param oldFragment The fragment to remove, if present.
     * @param newFragment The fragment to add, if present.
     */
    public void replace(@Nullable Fragment oldFragment, @Nullable Fragment newFragment, @NonNull ViewGroup container) {
        if (oldFragment != null) {
            oldFragment.remove();
        }
        if (newFragment != null) {
            newFragment.add(owner, container);
        }
    }

}
