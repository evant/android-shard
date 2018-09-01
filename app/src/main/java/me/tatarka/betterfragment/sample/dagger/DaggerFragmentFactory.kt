package me.tatarka.betterfragment.sample.dagger

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import me.tatarka.betterfragment.app.Fragment

@Singleton
class DaggerFragmentFactory @Inject constructor(
    private val fragmentMap: @JvmSuppressWildcards Map<Class<out Fragment>, Provider<Fragment>>
) : Fragment.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Fragment> newInstance(fragmentClass: Class<T>): T {
        return fragmentMap[fragmentClass]!!.get() as T
    }
}
