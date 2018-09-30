package me.tatarka.shard.sample

import androidx.viewpager.widget.ViewPager
import me.tatarka.shard.app.Shard
import me.tatarka.shard.pager.ShardPagerAdapter
import javax.inject.Inject

class ViewPagerShard @Inject constructor() : Shard() {

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_pager)
        val pager: ViewPager = requireViewById(R.id.pager)
        pager.adapter = object : ShardPagerAdapter(this) {
            override fun getItem(position: Int): Shard {
                return shardFactory.newInstance<MyShard>().withNumber(position)
            }

            override fun getCount(): Int {
                return 4
            }
        }
    }
}
