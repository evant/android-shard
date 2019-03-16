package me.tatarka.shard.pager.test;

import android.content.Context;

import androidx.annotation.NonNull;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.pager.ShardPagerAdapter;

public class TestPagerAdapter extends ShardPagerAdapter {

    public final Shard[] shards;
    public int getItemCalledCount;

    public TestPagerAdapter(Context context, Shard[] shards) {
        super(context);
        this.shards = shards;
    }

    @NonNull
    @Override
    public Shard getItem(int position) {
        getItemCalledCount += 1;
        return shards[position];
    }

    @Override
    public int getCount() {
        return shards.length;
    }
}
