package me.tatarka.betterfragment.sample.dagger

import android.content.Context
import android.util.AttributeSet
import me.tatarka.betterfragment.nav.FragmentNavHost

class DaggerFragmentNavHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FragmentNavHost(context, attrs) {
    init {
        fragmentFactory = injector.fragmentFactory
    }
}
