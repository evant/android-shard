package me.tatarka.betterfragment.sample.dagger;

import android.content.Context;
import android.util.AttributeSet;

import javax.inject.Inject;

import me.tatarka.betterfragment.widget.FragmentHost;

public class DaggerFragmentHost extends FragmentHost {
    @Inject
    DaggerFragmentFactory factory;

    public DaggerFragmentHost(Context context) {
        this(context, null);
    }

    public DaggerFragmentHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerAppComponent.INSTANCE.inject(this);
        setFragmentFactory(factory);
    }
}
