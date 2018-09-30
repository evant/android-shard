package me.tatarka.shard.pager.test;

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
import me.tatarka.shard.app.Shard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardPagerAdapterTest {

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
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Shard[]{new TestShard(), new TestShard(), new TestShard()});
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
        TestShard shard1 = new TestShard();
        shard1.state = 1;
        TestShard shard2 = new TestShard();
        shard2.state = 2;
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Shard[]{shard1, shard2});
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
        TestShard newShard1 = new TestShard();
        TestShard newShard2 = new TestShard();
        TestPagerAdapter newAdapter = new TestPagerAdapter(context, new Shard[]{newShard1, newShard2});
        newViewPager.setAdapter(newAdapter);
        newViewPager.restoreHierarchyState(state);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(newViewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assertEquals(1, ((TestShard) newAdapter.shards[0]).state);
        assertEquals(2, ((TestShard) newAdapter.shards[1]).state);
    }
}
