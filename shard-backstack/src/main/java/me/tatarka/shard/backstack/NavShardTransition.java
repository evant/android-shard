package me.tatarka.shard.backstack;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.TransitionRes;

public class NavShardTransition {

    final int transition;
    final int enterAnim;
    final int exitAnim;
    final int popEnterAnim;
    final int popExitAnim;

    NavShardTransition(int transition, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        this.transition = transition;
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        this.popEnterAnim = popEnterAnim;
        this.popExitAnim = popExitAnim;
    }

    public static NavShardTransition fromTransitionRes(@TransitionRes int transition) {
        return new NavShardTransition(transition, 0, 0, 0, 0);
    }

    public static NavShardTransition fromAnimRes(@AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int exitAnim) {
        return fromAnimRes(enterAnim, exitAnim, enterAnim, exitAnim);
    }

    public static NavShardTransition fromAnimRes(
            @AnimatorRes @AnimRes int enterAnim,
            @AnimatorRes @AnimRes int exitAnim,
            @AnimatorRes @AnimRes int popEnterAnim,
            @AnimatorRes @AnimRes int popExitAnim) {
        return new NavShardTransition(0, enterAnim, exitAnim, popEnterAnim, popExitAnim);
    }
}
