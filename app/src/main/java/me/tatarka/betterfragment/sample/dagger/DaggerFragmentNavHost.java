package me.tatarka.betterfragment.sample.dagger;

import android.content.Context;
import android.util.AttributeSet;

import javax.inject.Inject;

import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.nav.FragmentNavHost;

public class DaggerFragmentNavHost extends FragmentNavHost {

    @Inject
    Fragment.Factory fragmentFactory;

    public DaggerFragmentNavHost(Context context) {
        this(context, null);
    }

    public DaggerFragmentNavHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerAppComponent.INSTANCE.inject(this);
        setFragmentFactory(fragmentFactory);
    }
}
