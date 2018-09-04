package me.tatarka.betterfragment.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.R;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;
import me.tatarka.betterfragment.app.FragmentOwner;
import me.tatarka.betterfragment.app.FragmentOwners;
import me.tatarka.betterfragment.app.FragmentTransitionHelper;

public class FragmentHost extends FrameLayout {

    private final FragmentOwner owner;
    private final FragmentManager fm;
    private final FragmentTransitionHelper th;
    private Fragment.Factory factory = Fragment.DefaultFactory.getInstance();
    @Nullable
    private Fragment fragment;
    @Nullable
    private Class<? extends Fragment> initialFragmentClass;
    @Nullable
    private Animation exitAnimation;
    @Nullable
    private Animation enterAnimation;
    @Nullable
    private Transition transition;

    public FragmentHost(Context context) {
        this(context, null);
    }

    @SuppressLint("WrongConstant")
    public FragmentHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        owner = FragmentOwners.get(this);
        fm = new FragmentManager(owner);
        th = new FragmentTransitionHelper(fm);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentHost);
            String fragmentName = a.getString(R.styleable.FragmentHost_android_name);
            try {
                if (fragmentName != null) {
                    initialFragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            int enterAnimId = a.getResourceId(R.styleable.FragmentHost_enterAnim, 0);
            if (enterAnimId != 0) {
                enterAnimation = AnimationUtils.loadAnimation(context, enterAnimId);
            }
            int exitAnimId = a.getResourceId(R.styleable.FragmentHost_exitAnim, 0);
            if (exitAnimId != 0) {
                exitAnimation = AnimationUtils.loadAnimation(context, exitAnimId);
            }
            int transitionId = a.getResourceId(R.styleable.FragmentHost_transition, 0);
            if (transitionId != 0) {
                transition = TransitionInflater.from(context).inflateTransition(transitionId);
            }
            a.recycle();
        }
    }

    @Override
    @CallSuper
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (fragment == null && initialFragmentClass != null && !owner.getStateStore().isStateRestored()) {
            fragment = factory.newInstance(initialFragmentClass);
            fm.add(fragment, this);
        }
    }

    /**
     * A convenience method for {@code host.setFragment(host.getFragmentFactory().newInstance(fragmentClass))}
     */
    public void setFragmentClass(@Nullable Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null) {
            setFragment(null);
        } else {
            setFragment(factory.newInstance(fragmentClass));
        }
    }

    public void setFragment(@Nullable Fragment fragment) {
        Fragment oldFragment = this.fragment;
        this.fragment = fragment;
        if (transition != null) {
            th.replace(oldFragment, fragment, this, transition);
        } else {
            th.replace(oldFragment, fragment, this, enterAnimation, exitAnimation, FragmentTransitionHelper.NEW_FRAGMENT_ON_TOP);
        }
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

    public void setExitAnimation(@Nullable Animation animation) {
        exitAnimation = animation;
    }

    @Nullable
    public Animation getEnterAnimation() {
        return enterAnimation;
    }

    public void setEnterAnimation(@Nullable Animation animation) {
        enterAnimation = animation;
    }

    @Nullable
    public Animation getExitAnimation() {
        return exitAnimation;
    }

    public void setTransition(@Nullable Transition transition) {
        this.transition = transition;
    }

    @Nullable
    public Transition getTransition() {
        return transition;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), fragment != null ? fm.saveState(fragment) : null);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        final Fragment.State fragmentState = savedState.fragmentState;
        if (fragmentState != null) {
            fragment = factory.newInstance(fragmentState.getFragmentClass());
            fm.restoreState(fragment, fragmentState);
            fm.add(fragment, this);
        }
    }

    public static class SavedState extends BaseSavedState {
        @Nullable
        final Fragment.State fragmentState;

        SavedState(Parcelable superState, @Nullable Fragment.State fragmentState) {
            super(superState);
            this.fragmentState = fragmentState;
        }

        SavedState(Parcel source) {
            super(source);
            this.fragmentState = source.readParcelable(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
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
