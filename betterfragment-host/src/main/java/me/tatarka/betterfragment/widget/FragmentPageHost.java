package me.tatarka.betterfragment.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentManager;
import me.tatarka.betterfragment.FragmentOwner;
import me.tatarka.betterfragment.FragmentOwners;
import me.tatarka.betterfragment.FragmentTransitionHelper;
import me.tatarka.betterfragment.host.R;

public class FragmentPageHost extends FrameLayout {

    private final FragmentOwner owner;
    private final FragmentManager fm;
    private final FragmentTransitionHelper th;
    @Nullable
    private Adapter adapter;
    @Nullable
    private OnPageChangedListener listener;
    @Nullable
    private Fragment fragment;
    @IdRes
    private int startPage;
    @IdRes
    private int currentPage;
    private SparseArray<Fragment.State> fragmentStates = new SparseArray<>();
    @Nullable
    private Animation exitAnimation;
    @Nullable
    private Animation enterAnimation;

    public FragmentPageHost(@NonNull Context context) {
        this(context, null);
    }

    public FragmentPageHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        owner = FragmentOwners.get(this);
        fm = new FragmentManager(owner);
        th = new FragmentTransitionHelper(fm);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentPageHost);
            startPage = a.getResourceId(R.styleable.FragmentPageHost_startPage, startPage);
            int enterAnimId = a.getResourceId(R.styleable.FragmentPageHost_enterAnim, 0);
            if (enterAnimId != 0) {
                enterAnimation = AnimationUtils.loadAnimation(context, enterAnimId);
            }
            int exitAnimId = a.getResourceId(R.styleable.FragmentPageHost_exitAnim, 0);
            if (exitAnimId != 0) {
                exitAnimation = AnimationUtils.loadAnimation(context, exitAnimId);
            }
            a.recycle();
        }
    }

    @Override
    @CallSuper
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (fragment == null && startPage != 0 && !owner.willRestoreState()) {
            setCurrentPage(startPage, false);
        }
    }

    public void setAdapter(@Nullable Adapter adapter) {
        if (this.adapter == adapter) {
            return;
        }
        this.adapter = adapter;
        fragmentStates.clear();
        if (adapter != null && currentPage != 0) {
            setFragment(0, currentPage, adapter.newInstance(currentPage), false);
        }
    }

    private void setFragment(@IdRes int oldId, @IdRes int newId, @Nullable Fragment fragment, boolean animate) {
        Fragment oldFragment = this.fragment;
        this.fragment = fragment;
        if (oldFragment != null && oldId != 0) {
            fragmentStates.put(oldId, fm.saveState(oldFragment));
        }
        if (fragment != null && newId != 0) {
            fm.restoreState(fragment, fragmentStates.get(newId));
        }
        if (animate) {
            th.replace(oldFragment, fragment, this, enterAnimation, exitAnimation, FragmentTransitionHelper.NEW_FRAGMENT_ON_TOP);
        } else {
            fm.replace(oldFragment, fragment, this);
        }
        if (listener != null) {
            listener.onPageChanged(newId);
        }
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setCurrentPage(@IdRes int id) {
        setCurrentPage(id, isLaidOut());
    }

    private void setCurrentPage(@IdRes int id, boolean animate) {
        if (currentPage == id) {
            return;
        }
        int oldId = currentPage;
        currentPage = id;
        if (adapter != null) {
            setFragment(oldId, id, adapter.newInstance(id), animate);
        }
    }

    @IdRes
    public int getCurrentPage() {
        return currentPage;
    }

    @Nullable
    public Fragment getFragment() {
        return fragment;
    }

    public void setOnPageChangedListener(@Nullable OnPageChangedListener listener) {
        this.listener = listener;
        if (listener != null && currentPage != 0) {
            listener.onPageChanged(currentPage);
        }
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

    public interface Adapter {
        @Nullable
        Fragment newInstance(@IdRes int id);
    }

    public interface OnPageChangedListener {
        void onPageChanged(@IdRes int id);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), currentPage, fragmentStates);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPage = savedState.currentItem;
        fragmentStates = savedState.fragmentStates;

        if (adapter != null && currentPage != 0) {
            setFragment(0, currentPage, adapter.newInstance(currentPage), false);
        }
    }

    public static class SavedState extends BaseSavedState {
        final int currentItem;
        @Nullable
        final SparseArray fragmentStates;

        SavedState(Parcelable superState, int currentItem, SparseArray fragmentStates) {
            super(superState);
            this.currentItem = currentItem;
            this.fragmentStates = fragmentStates;
        }

        SavedState(Parcel source) {
            super(source);
            currentItem = source.readInt();
            fragmentStates = source.readSparseArray(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentItem);
            out.writeSparseArray(fragmentStates);
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
