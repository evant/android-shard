package me.tatarka.betterfragment.sample

import androidx.lifecycle.ViewModel
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.app.FragmentActivity
import me.tatarka.betterfragment.lifecycle.ViewModelProviders

inline fun <reified T : Fragment> Fragment.Factory.newInstance() = newInstance(T::class.java)

inline fun <reified T : ViewModel> FragmentActivity.viewModel() =
    ViewModelProviders.of(this)[T::class.java]

inline fun <reified T : ViewModel> Fragment.viewModel() = ViewModelProviders.of(this)[T::class.java]
