package me.tatarka.betterfragment.sample

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.host.FragmentPageHostUI
import me.tatarka.betterfragment.wiget.FragmentPageHost

inline fun <reified T : Fragment> Fragment.Factory.newInstance(args: Bundle = Bundle.EMPTY) =
    newInstance<T>(T::class.java.name, args)

inline fun BottomNavigationView.setupWithPageHost(host: FragmentPageHost) {
    FragmentPageHostUI.setupWithPageHost(this, host)
}

inline fun NavigationView.setupWithPageHost(host: FragmentPageHost) {
    FragmentPageHostUI.setupWithPageHost(this, host)
}