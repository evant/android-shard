package me.tatarka.shard.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import me.tatarka.shard.R;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.transition.ShardTransition;

public class ShardHost extends FrameLayout {

    private ShardOwner owner;
    private ShardManager fm;
    @Nullable
    private Shard shard;
    @Nullable
    private String initialName;
    @Nullable
    private ShardTransition defaultTransition;

    public ShardHost(Context context) {
        this(context, null);
    }

    @SuppressLint("WrongConstant")
    public ShardHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            owner = ShardOwners.get(context);
            fm = new ShardManager(owner);
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShardHost);
            initialName = a.getString(R.styleable.ShardHost_android_name);
            int transitionId = a.getResourceId(R.styleable.ShardHost_transition, 0);
            if (transitionId != 0) {
                defaultTransition = ShardTransition.fromTransitionRes(context, transitionId);
            } else {
                int enterAnimId = a.getResourceId(R.styleable.ShardHost_enterAnim, 0);
                int exitAnimId = a.getResourceId(R.styleable.ShardHost_exitAnim, 0);
                if (enterAnimId != 0 || exitAnimId != 0) {
                    defaultTransition = ShardTransition.fromAnimRes(context, enterAnimId, exitAnimId);
                }
            }
            a.recycle();
        }
        if (!isInEditMode()) {
            if (initialName != null && !ShardManager.isRestoringState(owner)) {
                shard = getShardFactory().newInstance(initialName);
                fm.add(shard, this);
            }
        } else {
            // If the shard is annotated we can show the layout in the preview.
            if (initialName != null) {
                try {
                    Class<?> shardClass = Class.forName(initialName);
                    ContentView contentView = shardClass.getAnnotation(ContentView.class);
                    if (contentView != null) {
                        inflate(context, contentView.value(), this);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setShard(@Nullable Shard shard) {
        setShard(shard, null);
    }

    public void setShard(@Nullable Shard shard, @Nullable ShardTransition transition) {
        Shard oldShard = this.shard;
        this.shard = shard;
        fm.replace(oldShard, shard, this, transition != null ? transition : defaultTransition);
    }

    @Nullable
    public Shard getShard() {
        return shard;
    }

    public void setDefaultTransition(@Nullable ShardTransition transition) {
        defaultTransition = transition;
    }

    @Nullable
    public ShardTransition getDefaultTransition() {
        return defaultTransition;
    }

    @NonNull
    public final Shard.Factory getShardFactory() {
        return owner.getShardFactory();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(),
                shard != null ? shard.getClass().getName() : null,
                shard != null ? fm.saveState(shard) : null);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        String name = savedState.name;
        Shard.State shardState = savedState.shardState;
        if (name != null && shardState != null) {
            shard = getShardFactory().newInstance(name);
            fm.restoreState(shard, shardState);
            fm.add(shard, this);
        }
    }

    public static class SavedState extends BaseSavedState {
        @Nullable
        final String name;
        @Nullable
        final Shard.State shardState;

        SavedState(Parcelable superState, @Nullable String name, @Nullable Shard.State shardState) {
            super(superState);
            this.name = name;
            this.shardState = shardState;
        }

        SavedState(Parcel source) {
            super(source);
            this.name = source.readString();
            this.shardState = source.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(name);
            out.writeParcelable(shardState, flags);
        }

        public static Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
