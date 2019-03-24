package me.tatarka.shard.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.TransitionRes;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.backstack.ShardBackStack;

@Navigator.Name("shard")
public class ShardNavigator extends Navigator<ShardNavigator.Destination> {

    private static final String STATE_BACK_STACK = "back_stack";

    private ShardBackStack backStack;
    private Shard.Factory factory;

    public ShardNavigator(FrameLayout container) {
        if (!container.isInEditMode()) {
            ShardOwner owner = ShardOwners.get(container.getContext());
            backStack = new ShardBackStack(owner, container);
            factory = owner.getShardFactory();
        }
    }

    @NonNull
    @Override
    public Destination createDestination() {
        return new Destination(this, factory);
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        Shard shard = destination.shardFactory.newInstance(destination.getName());
        shard.setArgs(args);
        backStack.push(shard, destination.getId(), convertNavOptions(navOptions, navigatorExtras));
        return backStack.willPerformAction() ? destination : null;
    }

    @Override
    public boolean popBackStack() {
        backStack.pop();
        return backStack.willPerformAction();
    }

    @Nullable
    @Override
    public Bundle onSaveState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_BACK_STACK, backStack.saveState());
        return bundle;
    }

    @Override
    public void onRestoreState(@NonNull Bundle savedState) {
        ShardBackStack.State state = savedState.getParcelable(STATE_BACK_STACK);
        backStack.restoreState(state);
    }

    public static class Destination extends NavDestination {

        private String name;
        private Shard.Factory shardFactory;

        Destination(Navigator<? extends NavDestination> navigator, Shard.Factory shardFactory) {
            super(navigator);
            this.shardFactory = shardFactory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShardNavigator);
            name = a.getString(R.styleable.ShardNavigator_android_name);
            a.recycle();
        }
    }

    private static me.tatarka.shard.backstack.NavOptions convertNavOptions(@Nullable NavOptions options, @Nullable Navigator.Extras navigationExtras) {
        me.tatarka.shard.backstack.NavOptions.Builder builder = new me.tatarka.shard.backstack.NavOptions.Builder();
        if (options != null) {
            builder.singleTop(options.shouldLaunchSingleTop());
            builder.animate(options.getEnterAnim(), options.getExitAnim(), options.getPopEnterAnim(), options.getPopExitAnim());
        }
        if (navigationExtras instanceof Extras) {
            builder.transition(((Extras) navigationExtras).transition);
        }
        return builder.build();
    }

    public static class Extras implements Navigator.Extras {
        @TransitionRes
        final int transition;

        Extras(@TransitionRes int transition) {
            this.transition = transition;
        }

        public static class Builder {
            @TransitionRes
            private int transition = 0;

            /**
             * Sets a {@link androidx.transition.Transition} animation when navigating. Note: this
             * uses the androidx version and not the framework version.
             */
            public Builder transition(@TransitionRes int transition) {
                this.transition = transition;
                return this;
            }

            public Extras build() {
                return new Extras(transition);
            }
        }
    }
}
