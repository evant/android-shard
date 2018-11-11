package me.tatarka.shard.transition;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.TransitionRes;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

public final class ShardTransitionCompat {

    @Nullable
    public static ShardTransition fromTransitionRes(@NonNull Context context, @TransitionRes int transition) {
        Transition t = (transition == 0 || transition == -1)
                ? null
                : TransitionInflater.from(context).inflateTransition(transition);
        return fromTransition(t);
    }

    @Nullable
    public static ShardTransition fromTransition(@Nullable Transition transition) {
        return transition != null ? new TransitionShardTransition(transition) : null;
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
}
