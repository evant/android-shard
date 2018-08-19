package me.tatarka.betterfragment.sample.dagger;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.sample.MyFragment;
import me.tatarka.betterfragment.sample.NavigationFragment;
import me.tatarka.betterfragment.sample.SimpleHostFragment;
import me.tatarka.betterfragment.sample.ViewPagerFragment;

@Module
public abstract class FragmentModule {

    @Binds
    abstract Fragment.Factory factory(DaggerFragmentFactory factory);

    @Binds
    @IntoMap
    @FragmentKey(MyFragment.class)
    abstract Fragment myFragment(MyFragment fragment);

    @Binds
    @IntoMap
    @FragmentKey(SimpleHostFragment.class)
    abstract Fragment simpleHostFragment(SimpleHostFragment fragment);

    @Binds
    @IntoMap
    @FragmentKey(NavigationFragment.class)
    abstract Fragment navigationFragment(NavigationFragment fragment);

    @Binds
    @IntoMap
    @FragmentKey(ViewPagerFragment.class)
    abstract Fragment viewPagerFragment(ViewPagerFragment fragment);
}
