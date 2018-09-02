package me.tatarka.betterfragment.sample

import android.view.View
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.widget.FragmentHost
import javax.inject.Inject

class SimpleHostFragment @Inject constructor(
    private val fragmentFactory: Fragment.Factory
) : Fragment() {

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.simple_host)
        val host: FragmentHost = requireViewById(R.id.host)
        requireViewById<View>(R.id.one).setOnClickListener {
            host.fragment =
                    fragmentFactory.newInstance<MyFragment>().withNumber(1)
        }
        requireViewById<View>(R.id.two).setOnClickListener {
            host.fragment =
                    fragmentFactory.newInstance<MyFragment>().withNumber(2)
        }
    }
}
