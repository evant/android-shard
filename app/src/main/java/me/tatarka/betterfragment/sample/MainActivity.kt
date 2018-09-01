package me.tatarka.betterfragment.sample

import android.os.Bundle
import androidx.lifecycle.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.tatarka.betterfragment.appcompat.app.AppCompatActivity
import me.tatarka.betterfragment.lifecycle.ViewModelProviders
import me.tatarka.betterfragment.sample.dagger.injector
import me.tatarka.betterfragment.widget.FragmentPageHost

class MainActivity : AppCompatActivity() {

    lateinit var pageHost: FragmentPageHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentFactory = injector.fragmentFactory

        ViewModelProviders.of(this).get<MyViewModel>()

        setContentView(R.layout.activity_main)
        pageHost = findViewById(R.id.page_host)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)!!
        pageHost.adapter = FragmentPageHost.Adapter { id ->
            when (id) {
                R.id.simple_host -> fragmentFactory.newInstance<SimpleHostFragment>()
                R.id.view_pager -> fragmentFactory.newInstance<ViewPagerFragment>()
                R.id.navigation -> fragmentFactory.newInstance<NavigationFragment>()
                else -> null
            }
        }
        bottomNav.setupWithPageHost(pageHost)
    }

    override fun onBackPressed() {
        val fragment = pageHost.fragment
        if (!(fragment is NavInterface && fragment.onBackPressed())) {
            super.onBackPressed()
        }
    }

    override fun onNavigateUp(): Boolean {
        val fragment = pageHost.fragment
        return if (fragment is NavInterface && fragment.onNavigateUp()) {
            true
        } else {
            super.onNavigateUp()
        }
    }
}
