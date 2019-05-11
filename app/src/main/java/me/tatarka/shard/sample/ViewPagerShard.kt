package me.tatarka.shard.sample

import androidx.viewpager.widget.ViewPager
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.newInstance
import me.tatarka.shard.pager.ShardPagerAdapter
import javax.inject.Inject

class ViewPagerShard @Inject constructor() : Shard(), Resetable {
    lateinit var pager: ViewPager

    override fun onCreate() {
        setContentView(R.layout.view_pager)
        pager = requireViewById(R.id.pager)
        pager.adapter = object : ShardPagerAdapter(this) {
            override fun getItem(position: Int): Shard {
                return shardFactory.newInstance<MyShard>().withNumber(position)
            }

            override fun getCount(): Int {
                return 4
            }
        }
    }

    override fun reset() {
        pager.currentItem = 0
    }
}
