package me.tatarka.betterfragment.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.R;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;
import me.tatarka.betterfragment.app.FragmentOwner;
import me.tatarka.betterfragment.app.FragmentOwners;
import me.tatarka.betterfragment.transition.FragmentTransition;

public class FragmentHost extends FrameLayout {

    private final FragmentOwner owner;
    private final FragmentManager fm;
    private Fragment.Factory factory = Fragment.DefaultFactory.getInstance();
    @Nullable
    private Fragment fragment;
    @Nullable
    private String initialName;
    @Nullable
    private FragmentTransition defaultTransition;

    public FragmentHost(Context context) {
        this(context, null);
    }

    @SuppressLint("WrongConstant")
    public FragmentHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        owner = FragmentOwners.get(this);
        fm = new FragmentManager(owner);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentHost);
            initialName = a.getString(R.styleable.FragmentHost_android_name);
            int transitionId = a.getResourceId(R.styleable.FragmentHost_transition, 0);
            if (transitionId != 0) {
                defaultTransition = FragmentTransition.fromTransitionRes(context, transitionId);
            } else {
                int enterAnimId = a.getResourceId(R.styleable.FragmentHost_enterAnim, 0);
                int exitAnimId = a.getResourceId(R.styleable.FragmentHost_exitAnim, 0);
                if (enterAnimId != 0 || exitAnimId != 0) {
                    defaultTransition = FragmentTransition.fromAnimRes(context, enterAnimId, exitAnimId);
                }
            }
            a.recycle();
        }
    }

    @Override
    @CallSuper
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (fragment == null && initialName != null && !owner.getInstanceStateStore().isStateRestored()) {
            fragment = factory.newInstance(initialName, Bundle.EMPTY);
            fm.add(fragment, this);
        }
    }

    public void setFragment(@Nullable Fragment fragment) {
        setFragment(fragment, null);
    }

    public void setFragment(@Nullable Fragment fragment, @Nullable FragmentTransition transition) {
        Fragment oldFragment = this.fragment;
        this.fragment = fragment;
        fm.replace(oldFragment, fragment, this, transition != null ? transition : defaultTransition);
    }

    @Nullable
    public Fragment getFragment() {
        return fragment;
    }

    public void setFragmentFactory(@NonNull Fragment.Factory factory) {
        this.factory = factory;
    }

    @NonNull
    public Fragment.Factory getFragmentFactory() {
        return factory;
    }

    public void setDefaultTransition(@Nullable FragmentTransition transition) {
        defaultTransition = transition;
    }

    @Nullable
    public FragmentTransition getDefaultTransition() {
        return defaultTransition;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(),
                fragment != null ? fragment.getClass().getName() : null,
                fragment != null ? fm.saveState(fragment) : null);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        String name = savedState.name;
        Fragment.State fragmentState = savedState.fragmentState;
        if (name != null) {
            fragment = factory.newInstance(name, fragmentState.getArgs());
            fm.restoreState(fragment, fragmentState);
            fm.add(fragment, this);
        }
    }

    public static class SavedState extends BaseSavedState {
        @Nullable
        final String name;
        @Nullable
        final Fragment.State fragmentState;

        SavedState(Parcelable superState, @Nullable String name, @Nullable Fragment.State fragmentState) {
            super(superState);
            this.name = name;
            this.fragmentState = fragmentState;
        }

        SavedState(Parcel source) {
            super(source);
            this.name = source.readString();
            this.fragmentState = source.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(name);
            out.writeParcelable(fragmentState, flags);
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
