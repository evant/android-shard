package me.tatarka.betterfragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class FragmentTransitionHelper {

    public static final int NEW_FRAGMENT_ON_TOP = 0;
    public static final int OLD_FRAGMENT_ON_TOP = 1;

    private final FragmentManager fm;

    public FragmentTransitionHelper(FragmentManager fm) {
        this.fm = fm;
    }

    /**
     * Replaces the fragment, running an animation.
     *
     * @param oldFragment The fragment to remove, if present.
     * @param newFragment The fragment to add, if present.
     * @param enterAnim   Animator to animate the add of the new fragment, if present. 0 may be passed for no animation.
     * @param exitAnim    Animator to animate the remove of the old fragment, if present. 0 may be passed for no animation.
     * @param zOrder      Which fragment should be shown on top while the animation is running.
     */
    public void replace(@Nullable Fragment oldFragment,
                        @Nullable Fragment newFragment,
                        @NonNull final ViewGroup container,
                        @AnimRes @AnimatorRes int enterAnim,
                        @AnimRes @AnimatorRes int exitAnim,
                        @ZOrder int zOrder) {

        Object enter = enterAnim != 0 ? AnimUtil.loadAnim(container.getContext(), enterAnim) : null;
        Object exit = exitAnim != 0 ? AnimUtil.loadAnim(container.getContext(), exitAnim) : null;
        if (enter instanceof Animation) {
            replace(oldFragment, newFragment, container, (Animation) enter, (Animation) exit, zOrder);
        } else {
            replace(oldFragment, newFragment, container, (Animator) enter, (Animator) exit, zOrder);
        }
    }

    /**
     * Replaces the fragment, running an animation.
     *
     * @param oldFragment   The fragment to remove, if present.
     * @param newFragment   The fragment to add, if present.
     * @param enterAnimator Animator to animate the add of the new fragment, if present. 0 may be passed for no animation.
     * @param exitAnimator  Animator to animate the remove of the old fragment, if present. 0 may be passed for no animation.
     * @param zOrder        Which fragment should be shown on top while the animation is running.
     */
    public void replace(@Nullable Fragment oldFragment,
                        @Nullable Fragment newFragment,
                        @NonNull final ViewGroup container,
                        @Nullable Animator enterAnimator,
                        @Nullable Animator exitAnimator,
                        @ZOrder int zOrder) {
        if (oldFragment == null && newFragment == null) {
            return;
        }
        if (enterAnimator == null && exitAnimator == null) {
            fm.replace(oldFragment, newFragment, container);
        } else {
            View oldView = null;
            if (oldFragment != null) {
                final View view = oldView = oldFragment.getView();
                if (exitAnimator != null) {
                    exitAnimator.setTarget(view);
                    container.startViewTransition(view);
                    exitAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            container.endViewTransition(view);
                        }
                    });
                }
            }
            fm.replace(oldFragment, newFragment, container);
            if (newFragment != null) {
                if (enterAnimator != null) {
                    enterAnimator.setTarget(newFragment.getView());
                }
                if (oldFragment != null) {
                    if (zOrder == OLD_FRAGMENT_ON_TOP) {
                        oldView.bringToFront();
                    }
                }
            }
            if (exitAnimator != null) {
                exitAnimator.start();
            }
            if (enterAnimator != null) {
                enterAnimator.start();
            }
        }
    }

    /**
     * Replaces the fragment, running an animation.
     *
     * @param oldFragment    The fragment to remove, if present.
     * @param newFragment    The fragment to add, if present.
     * @param enterAnimation Animator to animate the add of the new fragment, if present. 0 may be passed for no animation.
     * @param exitAnimation  Animator to animate the remove of the old fragment, if present. 0 may be passed for no animation.
     * @param zOrder         Which fragment should be shown on top while the animation is running.
     */
    public void replace(@Nullable Fragment oldFragment,
                        @Nullable Fragment newFragment,
                        @NonNull final ViewGroup container,
                        @Nullable Animation enterAnimation,
                        @Nullable Animation exitAnimation,
                        @ZOrder int zOrder) {
        if (oldFragment == null && newFragment == null) {
            return;
        }
        if (enterAnimation == null && exitAnimation == null) {
            fm.replace(oldFragment, newFragment, container);
        } else {
            View oldView = null;
            if (oldFragment != null) {
                final View view = oldView = oldFragment.getView();
                if (exitAnimation != null) {
                    container.startViewTransition(view);
                    exitAnimation.setAnimationListener(new AnimationListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            container.endViewTransition(view);
                        }
                    });
                }
            }
            fm.replace(oldFragment, newFragment, container);
            if (newFragment != null && oldFragment != null && zOrder == OLD_FRAGMENT_ON_TOP) {
                oldView.bringToFront();
            }
            if (oldFragment != null && exitAnimation != null) {
                oldView.startAnimation(exitAnimation);
                exitAnimation.start();
            }
            if (enterAnimation != null) {
                newFragment.getView().startAnimation(enterAnimation);
                enterAnimation.start();
            }
        }
    }

    @IntDef({NEW_FRAGMENT_ON_TOP, OLD_FRAGMENT_ON_TOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ZOrder {
    }

    static abstract class AnimationListenerAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
