package me.tatarka.betterfragment.sample

import androidx.viewpager.widget.ViewPager
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.pager.FragmentPagerAdapter
import me.tatarka.betterfragment.sample.dagger.DaggerFragmentFactory
import javax.inject.Inject

class ViewPagerFragment @Inject constructor(
    private val fragmentFactory: DaggerFragmentFactory
) : Fragment() {

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_pager)
        val pager: ViewPager = requireViewById(R.id.pager)
        pager.adapter = object : FragmentPagerAdapter(this) {
            override fun getItem(position: Int): Fragment {
                return fragmentFactory.newInstance<MyFragment>().withNumber(position)
            }

            override fun getCount(): Int {
                return 4
            }
        }
    }
}
