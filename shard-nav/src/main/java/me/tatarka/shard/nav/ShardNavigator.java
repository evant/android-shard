package me.tatarka.shard.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.transition.ShardTransition;

@Navigator.Name("shard")
public class ShardNavigator extends OptimizingNavigator<ShardNavigator.Destination, ShardNavigator.Page, ShardNavigator.PageState> {

    private final ShardManager fm;
    private final FrameLayout container;
    private Shard.Factory factory;

    public ShardNavigator(FrameLayout container) {
        this.container = container;
        ShardOwner owner = ShardOwners.get(container.getContext());
        fm = new ShardManager(owner);
        factory = owner.getShardFactory();
    }

    public void setShardFactory(@NonNull Shard.Factory factory) {
        this.factory = factory;
    }

    @NonNull
    public Shard.Factory getShardFactory() {
        return factory;
    }

    @NonNull
    @Override
    public Destination createDestination() {
        return new Destination(this, factory);
    }

    @NonNull
    @Override
    protected Page createPage(Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Extras navExtras) {
        Shard shard = destination.shardFactory.newInstance(destination.getName(), args != null ? args : Bundle.EMPTY);
        shard.setArgs(args);
        return new Page(factory, shard, navOptions);
    }

    @Override
    protected void replace(@Nullable Page oldPage, @NonNull Page newPage, int backStackEffect) {
        Shard oldShard = oldPage != null ? oldPage.shard : null;
        Shard newShard = newPage.shard;
        int enterAnim = -1;
        int exitAnim = -1;
        switch (backStackEffect) {
            case Navigator.BACK_STACK_DESTINATION_ADDED:
                enterAnim = newPage.enterAnim;
                exitAnim = newPage.exitAnim;
                break;
            case Navigator.BACK_STACK_DESTINATION_POPPED:
                if (oldPage != null) {
                    enterAnim = oldPage.popEnterAnim;
                    exitAnim = oldPage.popExitAnim;
                }
                break;
        }
        ShardTransition transition = ShardTransition.fromAnimRes(container.getContext(), enterAnim, exitAnim);
        fm.replace(oldShard, newShard, container, transition);
    }

    @NonNull
    @Override
    protected PageState savePageState(Page page) {
        return page.saveState(fm);
    }

    @NonNull
    @Override
    protected Page restorePageState(PageState state) {
        return state.restore(fm, factory);
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

    static class Page {
        final Shard.Factory factory;
        final Shard shard;
        final int enterAnim;
        final int exitAnim;
        final int popEnterAnim;
        final int popExitAnim;

        Page(Shard.Factory factory, Shard shard, @Nullable NavOptions navOptions) {
            this.factory = factory;
            this.shard = shard;
            enterAnim = navOptions != null ? navOptions.getEnterAnim() : 0;
            exitAnim = navOptions != null ? navOptions.getExitAnim() : 0;
            popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : 0;
            popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : 0;
        }

        Page(Shard.Factory factory, Shard shard, PageState state) {
            this.factory = factory;
            this.shard = shard;
            enterAnim = 0;
            exitAnim = 0;
            popEnterAnim = state.popEnterAnim;
            popExitAnim = state.popExitAnim;
        }

        PageState saveState(ShardManager fm) {
            return new PageState(shard.getClass().getName(), fm.saveState(shard), popEnterAnim, popExitAnim);
        }
    }

    static class PageState implements Parcelable {
        final String name;
        final Shard.State state;
        final int popEnterAnim;
        final int popExitAnim;

        PageState(String name, Shard.State state, int popEnterAnim, int popExitAnim) {
            this.name = name;
            this.state = state;
            this.popEnterAnim = popEnterAnim;
            this.popExitAnim = popExitAnim;
        }

        PageState(Parcel in) {
            name = in.readString();
            state = in.readParcelable(Shard.State.class.getClassLoader());
            popEnterAnim = in.readInt();
            popExitAnim = in.readInt();
        }

        Page restore(ShardManager fm, Shard.Factory factory) {
            Shard shard = factory.newInstance(name, state.getArgs());
            fm.restoreState(shard, state);
            return new Page(factory, shard, this);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeParcelable(state, flags);
            dest.writeInt(popEnterAnim);
            dest.writeInt(popExitAnim);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<PageState> CREATOR = new Creator<PageState>() {
            @Override
            public PageState createFromParcel(Parcel in) {
                return new PageState(in);
            }

            @Override
            public PageState[] newArray(int size) {
                return new PageState[size];
            }
        };
    }
}
