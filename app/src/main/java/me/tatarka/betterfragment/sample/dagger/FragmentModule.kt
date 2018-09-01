package me.tatarka.betterfragment.sample.dagger

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.sample.MyFragment
import me.tatarka.betterfragment.sample.NavigationFragment
import me.tatarka.betterfragment.sample.SimpleHostFragment
import me.tatarka.betterfragment.sample.ViewPagerFragment

@Module
abstract class FragmentModule {

    @Binds
    internal abstract fun factory(factory: DaggerFragmentFactory): Fragment.Factory

    @Binds
    @IntoMap
    @FragmentKey(MyFragment::class)
    internal abstract fun myFragment(fragment: MyFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(SimpleHostFragment::class)
    internal abstract fun simpleHostFragment(fragment: SimpleHostFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(NavigationFragment::class)
    internal abstract fun navigationFragment(fragment: NavigationFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ViewPagerFragment::class)
    internal abstract fun viewPagerFragment(fragment: ViewPagerFragment): Fragment
}
