package me.tatarka.betterfragment.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.state.InstanceStateRegistry;
import me.tatarka.betterfragment.transition.FragmentTransition;

public final class FragmentManager {

    private final FragmentOwner owner;

    public FragmentManager(@NonNull FragmentOwner owner) {
        this.owner = owner;
    }

    /**
     * Adds the fragment, attaching is to the given container and calling {@link Fragment#onCreate()}.
     * This method is a no-op if the fragment is already destroyed.
     *
     * @throws IllegalStateException If the fragment has already been created or has been created.
     */
    public void add(@NonNull Fragment fragment, @NonNull ViewGroup container) {
        fragment.add(owner, new ViewGroupContainer(container));
    }

    public void add(@NonNull Fragment fragment, @NonNull Fragment.Container container) {
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
     * so that when {@link InstanceStateRegistry#onSaveInstanceState()} is called is consistent. Therefore you
     * should only call this method when the fragment is being stopped or destroyed.
     *
     * @throws IllegalStateException If the fragment is destroyed.
     */
    @NonNull
    public Fragment.State saveState(Fragment fragment) {
        return fragment.saveInstanceState();
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

    public void replace(@Nullable Fragment oldFragment,
                        @Nullable Fragment newFragment,
                        @NonNull FrameLayout container) {
        replace(oldFragment, newFragment, container, null);
    }

    /**
     * Replaces the fragment.
     *
     * @param oldFragment The fragment to remove, if present.
     * @param newFragment The fragment to add, if present.
     */
    public void replace(@Nullable Fragment oldFragment,
                        @Nullable Fragment newFragment,
                        @NonNull FrameLayout container,
                        @Nullable FragmentTransition transition) {
        if (!container.isLaidOut()) {
            transition = null;
        }
        if (oldFragment != null) {
            ViewGroup oldView = oldFragment.getView();
            if (transition != null && oldView != null) {
                transition.captureBefore(oldView);
            }
            oldFragment.remove();
        }
        if (newFragment != null) {
            newFragment.add(owner, new ViewGroupContainer(container));
            ViewGroup newView = newFragment.getView();
            if (transition != null && newView != null) {
                transition.captureAfter(newView);
            }
            if (transition != null) {
                transition.start();
            }
        }
    }

    static class ViewGroupContainer implements Fragment.Container {
        private final ViewGroup frame;

        ViewGroupContainer(ViewGroup frame) {
            this.frame = frame;
        }

        @Override
        public void addView(View view) {
            frame.addView(view);
        }

        @Override
        public void removeView(View view) {
            frame.removeView(view);
        }
    }
}
