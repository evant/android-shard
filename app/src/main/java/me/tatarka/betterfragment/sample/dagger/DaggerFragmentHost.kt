package me.tatarka.betterfragment.sample.dagger

import android.content.Context
import android.util.AttributeSet

import javax.inject.Inject

import me.tatarka.betterfragment.widget.FragmentHost

class DaggerFragmentHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FragmentHost(context, attrs) {
    init {
        fragmentFactory = injector.fragmentFactory
    }
}
