package me.tatarka.shard.backstack;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.IdRes;
import androidx.annotation.TransitionRes;

public class NavOptions {

    final int transition;
    final int enterAnim;
    final int exitAnim;
    final int popEnterAnim;
    final int popExitAnim;
    final boolean singleTop;

    NavOptions(int transition, int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim, boolean singleTop) {
        this.transition = transition;
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        this.popEnterAnim = popEnterAnim;
        this.popExitAnim = popExitAnim;
        this.singleTop = singleTop;
    }

    public static class Builder {
        private int enterAnim;
        private int exitAnim;
        private int popEnterAnim;
        private int popExitAnim;
        private int transition;
        private boolean singleTop;

        public Builder singleTop(boolean singleTop) {
            this.singleTop = singleTop;
            return this;
        }

        public Builder animate(@AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int exitAnim) {
            return animate(enterAnim, exitAnim, enterAnim, exitAnim);
        }

        public Builder animate(
                @AnimatorRes @AnimRes int enterAnim,
                @AnimatorRes @AnimRes int exitAnim,
                @AnimatorRes @AnimRes int popEnterAnim,
                @AnimatorRes @AnimRes int popExitAnim) {
            this.enterAnim = enterAnim;
            this.exitAnim = exitAnim;
            this.popEnterAnim = popEnterAnim;
            this.popExitAnim = popExitAnim;
            return this;
        }

        public Builder transition(@TransitionRes int transition) {
            this.transition = transition;
            return this;
        }

        public NavOptions build() {
            return new NavOptions(transition, enterAnim, exitAnim, popEnterAnim, popExitAnim, singleTop);
        }
    }
}
