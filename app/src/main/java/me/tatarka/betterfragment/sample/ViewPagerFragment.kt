package me.tatarka.betterfragment.sample

import android.os.Bundle

import javax.inject.Inject
import androidx.viewpager.widget.ViewPager
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.pager.FragmentPagerAdapter

class ViewPagerFragment @Inject constructor(
    private val fragmentFactory: Fragment.Factory
) : Fragment() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
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
