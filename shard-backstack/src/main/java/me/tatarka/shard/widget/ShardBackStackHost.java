package me.tatarka.shard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardOwner;
import me.tatarka.shard.app.ShardOwners;
import me.tatarka.shard.backstack.R;
import me.tatarka.shard.backstack.ShardBackStack;

public class ShardBackStackHost extends FrameLayout {

    private ShardOwner owner;
    private ShardBackStack backStack;
    private OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            backStack.pop().commit();
        }
    };

    public ShardBackStackHost(@NonNull Context context) {
        this(context, null);
    }

    public ShardBackStackHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            owner = ShardOwners.get(context);
            backStack = new ShardBackStack(owner, this);
            backStack.addOnNavigatedListener(new ShardBackStack.OnNavigatedListener() {
                @Override
                public void onNavigated(Shard shard, int id) {
                    backPressedCallback.setEnabled(backStack.size() > 0);
                }
            });
        }
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShardBackStackHost);
            if (!isInEditMode()) {
                String name = a.getString(R.styleable.ShardBackStackHost_startingShard);
                int id = a.getInt(R.styleable.ShardBackStackHost_startingId, ShardBackStack.NO_ID);
                if (name != null) {
                    Shard shard = owner.getShardFactory().newInstance(name);
                    backStack.setStarting(shard, id);
                }
            }
            if (isInEditMode()) {
                int layout = a.getResourceId(R.styleable.ShardBackStackHost_android_layout, 0);
                if (layout != 0) {
                    inflate(context, layout, this);
                }
            }
            a.recycle();
        }
    }

    @Override
    @CallSuper
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        owner.getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    @Override
    @CallSuper
    protected void onDetachedFromWindow() {
        backPressedCallback.remove();
        super.onDetachedFromWindow();
    }

    @MainThread
    public final ShardBackStack getBackStack() {
        return backStack;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), backStack.saveState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        backStack.restoreState(savedState.backStackState);
    }

    public static class SavedState extends BaseSavedState {

        private final ShardBackStack.State backStackState;

        SavedState(Parcelable superState, ShardBackStack.State backStackState) {
            super(superState);
            this.backStackState = backStackState;
        }

        SavedState(Parcel source) {
            super(source);
            this.backStackState = source.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(backStackState, flags);
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
