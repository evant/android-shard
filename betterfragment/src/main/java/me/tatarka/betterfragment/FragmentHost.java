package me.tatarka.betterfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class FragmentHost extends FrameLayout {

    private final FragmentOwner owner;
    private final Fragment.Factory factory;
    @Nullable
    private Fragment fragment;
    @Nullable
    private Class<? extends Fragment> initialFragmentClass;

    public FragmentHost(Context context) {
        this(context, DefaultFragmentFactory.getInstance());
    }

    @SuppressLint("WrongConstant")
    public FragmentHost(Context context, Fragment.Factory fragmentFactory) {
        super(context);
        owner = FragmentOwners.get(this);
        factory = fragmentFactory;
    }

    @SuppressLint("WrongConstant")
    public FragmentHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        owner = FragmentOwners.get(this);
        Fragment.Factory factory = DefaultFragmentFactory.getInstance();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FragmentHost);
        String factoryName = a.getString(R.styleable.FragmentHost_fragmentFactory);
        String fragmentName = a.getString(R.styleable.FragmentHost_android_name);
        try {
            if (factoryName != null) {
                factory = (Fragment.Factory) Class.forName(factoryName).newInstance();
            }
            if (fragmentName != null) {
                initialFragmentClass = (Class<? extends Fragment>) Class.forName(fragmentName);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        a.recycle();
        this.factory = factory;
        if (!isInEditMode()) {
            if (initialFragmentClass != null && !owner.willRestoreState()) {
                fragment = this.factory.newInstance(initialFragmentClass);
                fragment.create(owner, this);
            }
        }
    }

    public void setFragment(@Nullable Fragment fragment) {
        if (this.fragment != null) {
            this.fragment.destroy();
        }
        this.fragment = fragment;
        if (fragment != null) {
            fragment.create(owner, this);
        }
    }

    @Nullable
    public Fragment getFragment() {
        return fragment;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), fragment != null ? fragment.saveState() : null);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        final Fragment.State fragmentState = savedState.fragmentState;
        if (fragmentState != null) {
            fragment = factory.newInstance(fragmentState.getFragmentClass());
            fragment.create(owner, this, fragmentState);
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
