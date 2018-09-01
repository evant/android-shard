package me.tatarka.betterfragment.sample

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.host.FragmentPageHostUI
import me.tatarka.betterfragment.widget.FragmentPageHost

inline fun <reified T : Fragment> Fragment.Factory.newInstance() = newInstance(T::class.java)

inline fun BottomNavigationView.setupWithPageHost(host: FragmentPageHost) {
    FragmentPageHostUI.setupWithPageHost(this, host)
}

inline fun NavigationView.setupWithPageHost(host: FragmentPageHost) {
    FragmentPageHostUI.setupWithPageHost(this, host)
}