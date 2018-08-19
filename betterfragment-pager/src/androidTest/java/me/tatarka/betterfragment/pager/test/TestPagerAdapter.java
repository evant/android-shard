package me.tatarka.betterfragment.pager.test;

import android.content.Context;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.pager.FragmentPagerAdapter;

public class TestPagerAdapter extends FragmentPagerAdapter {

    public final Fragment[] fragments;
    public int getItemCalledCount;

    public TestPagerAdapter(Context context, Fragment[] fragments) {
        super(context);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        getItemCalledCount += 1;
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
