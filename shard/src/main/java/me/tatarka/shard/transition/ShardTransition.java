package me.tatarka.shard.transition;

import android.animation.Animator;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.TransitionRes;
import androidx.collection.ArrayMap;

import static me.tatarka.shard.transition.AnimUtil.loadAnim;

public abstract class ShardTransition {

    public abstract void captureBefore(ViewGroup view);

    public abstract void captureAfter(ViewGroup view);

    public abstract void start();

    @Nullable
    public static ShardTransition fromTransitionRes(@NonNull Context context, @TransitionRes int transition) {
        Transition t = (transition == 0 || transition == -1)
                ? null
                : TransitionInflater.from(context).inflateTransition(transition);
        return fromTransition(t);
    }

    @Nullable
    public static ShardTransition fromTransition(final @Nullable Transition transition) {
        return transition != null ? new TransitionShardTransition(transition) : null;
    }

    @Nullable
    public static ShardTransition fromAnimRes(@NonNull Context context, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int exitAnim) {
        Object enter = loadAnim(context, enterAnim);
        Object exit = loadAnim(context, exitAnim);
        if ((enter == null || enter instanceof Animation) && (exit == null || exit instanceof Animation)) {
            return fromAnimations((Animation) enter, (Animation) exit);
        } else if ((enter == null || enter instanceof Animator) && (exit == null || exit instanceof Animator)) {
            return fromAnimators((Animator) enter, (Animator) exit);
        } else {
            throw new IllegalArgumentException("Resources must be of the same animation type. enter is " + animTypeName(enter) + " exit is " + animTypeName(exit));
        }
    }

    private static String animTypeName(Object anim) {
        return anim instanceof Animation ? "anim" : "animator";
    }

    @Nullable
    public static ShardTransition fromAnimations(@Nullable Animation enterAnimation, @Nullable final Animation exitAnimation) {
        if (enterAnimation == null && exitAnimation == null) {
            return null;
        }
        return new AnimationsShardTransition(enterAnimation, exitAnimation);
    }

    @Nullable
    public static ShardTransition fromAnimators(@Nullable Animator exitAnimator, @Nullable Animator enterAnimator) {
        if (enterAnimator == null && exitAnimator == null) {
            return null;
        }
        return new AnimatorsShardTransition(exitAnimator, enterAnimator);
    }

    static class TransitionShardTransition extends ShardTransition {

        @Nullable
        final Transition transition;

        TransitionShardTransition(@Nullable Transition transition) {
            this.transition = transition;
        }

        @Override
        public void captureBefore(ViewGroup view) {
            TransitionManager.beginDelayedTransition((ViewGroup) view.getParent(), transition);
        }

        @Override
        public void captureAfter(ViewGroup view) {
        }

        @Override
        public void start() {

        }
    }

    static class AnimationsShardTransition extends ShardTransition implements Animation.AnimationListener {
        @Nullable
        final Animation enterAnimation;
        @Nullable
        final Animation exitAnimation;
        ViewGroup container;
        View oldView;
        View newView;

        AnimationsShardTransition(@Nullable Animation enterAnimation, @Nullable Animation exitAnimation) {
            this.enterAnimation = enterAnimation;
            this.exitAnimation = exitAnimation;
        }

        @Override
        public void captureBefore(ViewGroup view) {
            if (exitAnimation != null) {
                container = (ViewGroup) view.getParent();
                if (oldView != null) {
                    // Ensure previous transition was ended so we don't leak.
                    container.endViewTransition(oldView);
                }
                oldView = view;
                container.startViewTransition(view);
                exitAnimation.setAnimationListener(this);
            }
        }

        @Override
        public void captureAfter(ViewGroup view) {
            if (enterAnimation != null) {
                newView = view;
            }
        }

        @Override
        public void start() {
            if (exitAnimation != null) {
                oldView.startAnimation(exitAnimation);
                exitAnimation.start();
            }
            if (enterAnimation != null) {
                newView.startAnimation(enterAnimation);
                enterAnimation.start();
            }
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            container.endViewTransition(oldView);
            container = null;
            oldView = null;
            newView = null;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    static class AnimatorsShardTransition extends ShardTransition implements Animator.AnimatorListener {
        @Nullable
        final Animator exitAnimator;
        @Nullable
        final Animator enterAnimator;
        ViewGroup container;
        View oldView;

        AnimatorsShardTransition(@Nullable Animator exitAnimator, @Nullable Animator enterAnimator) {
            this.exitAnimator = exitAnimator;
            this.enterAnimator = enterAnimator;
        }

        @Override
        public void captureBefore(ViewGroup view) {
            if (exitAnimator != null) {
                exitAnimator.setTarget(view);
                container = (ViewGroup) view.getParent();
                if (oldView != null) {
                    // Ensure previous transition was ended so we don't leak.
                    container.endViewTransition(oldView);
                }
                oldView = view;
                container.startViewTransition(view);
                exitAnimator.addListener(this);
            }
        }

        @Override
        public void captureAfter(ViewGroup view) {
            if (enterAnimator != null) {
                enterAnimator.setTarget(view);
            }
        }

        @Override
        public void start() {
            if (exitAnimator != null) {
                exitAnimator.start();
            }
            if (enterAnimator != null) {
                enterAnimator.start();
            }
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            container.endViewTransition(oldView);
            container = null;
            oldView = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
