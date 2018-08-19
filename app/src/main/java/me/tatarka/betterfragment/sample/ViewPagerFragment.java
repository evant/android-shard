package me.tatarka.betterfragment.sample;

import android.os.Bundle;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.pager.FragmentPagerAdapter;

public class ViewPagerFragment extends Fragment {

    private final Factory fragmentFactory;

    @Inject
    public ViewPagerFragment(Factory factory) {
        fragmentFactory = factory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.view_pager);
        ViewPager pager = requireViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(this) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentFactory.newInstance(MyFragment.class).withNumber(position);
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
    }
}
