package me.tatarka.betterfragment.sample.dagger

import android.os.Bundle
import me.tatarka.betterfragment.app.Fragment
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerFragmentFactory @Inject constructor(
    fragmentMap: @JvmSuppressWildcards Map<Class<out Fragment>, Provider<Fragment>>
) : Fragment.Factory {

    private val fragmentMap = fragmentMap.mapKeys { it.key.name }

    override fun <T : Fragment> newInstance(name: String, args: Bundle): T =
        fragmentMap.getValue(name).get() as T
}
