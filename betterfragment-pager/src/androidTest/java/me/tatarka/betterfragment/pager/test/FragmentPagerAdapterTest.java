package me.tatarka.betterfragment.pager.test;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.viewpager.widget.ViewPager;
import me.tatarka.betterfragment.Fragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FragmentPagerAdapterTest {

    Context context;

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Before
    public void setup() {
        context = activityTestRule.getActivity();
    }

    @Test
    public void createsPages() {
        final ViewPager viewPager = new ViewPager(context);
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Fragment[]{new TestFragment(), new TestFragment(), new TestFragment()});
        viewPager.setAdapter(adapter);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assertEquals(2, adapter.getItemCalledCount);
    }

    @Test
    public void savesAndRestoresStateOnConfigChange() {
        final ViewPager viewPager = new ViewPager(context);
        viewPager.setId(2);
        TestFragment fragment1 = new TestFragment();
        fragment1.state = 1;
        TestFragment fragment2 = new TestFragment();
        fragment2.state = 2;
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Fragment[]{fragment1, fragment2});
        viewPager.setAdapter(adapter);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        SparseArray<Parcelable> state = new SparseArray<>();
        viewPager.saveHierarchyState(state);
        final ViewPager newViewPager = new ViewPager(context);
        newViewPager.setId(2);
        TestFragment newFragment1 = new TestFragment();
        TestFragment newFragment2 = new TestFragment();
        TestPagerAdapter newAdapter = new TestPagerAdapter(context, new Fragment[]{newFragment1, newFragment2});
        newViewPager.setAdapter(newAdapter);
        newViewPager.restoreHierarchyState(state);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(newViewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assertEquals(1, ((TestFragment) newAdapter.fragments[0]).state);
        assertEquals(2, ((TestFragment) newAdapter.fragments[1]).state);
    }
}
