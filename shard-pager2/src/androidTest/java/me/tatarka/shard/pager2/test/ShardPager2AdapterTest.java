package me.tatarka.shard.pager2.test;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.viewpager2.widget.ViewPager2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShardPager2AdapterTest {
    Context context;

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Before
    public void setup() {
        context = activityTestRule.getActivity();
    }

    @Test
    public void createsPages() throws Throwable {
        final ViewPager2 viewPager = new ViewPager2(context);
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Shard[]{new TestShard(), new TestShard(), new TestShard()});
        viewPager.setAdapter(adapter);

        showViewPager(viewPager);
        goToPage(viewPager, 0);
        goToPage(viewPager, 1);

        assertEquals(2, adapter.getItemCalledCount);
    }

    @Test
    public void savesAndRestoresStateOnConfigChange() throws Throwable {
        final ViewPager2 viewPager = new ViewPager2(context);
        int id = ViewCompat.generateViewId();
        viewPager.setId(id);
        TestShard shard1 = new TestShard();
        shard1.state = 1;
        TestShard shard2 = new TestShard();
        shard2.state = 2;
        TestPagerAdapter adapter = new TestPagerAdapter(context, new Shard[]{shard1, shard2});
        viewPager.setAdapter(adapter);

        showViewPager(viewPager);
        goToPage(viewPager, 1);

        SparseArray<Parcelable> state = new SparseArray<>();
        viewPager.saveHierarchyState(state);

        removeViewPager(viewPager);

        final ViewPager2 newViewPager = new ViewPager2(context);
        newViewPager.setId(id);
        TestShard newShard1 = new TestShard();
        TestShard newShard2 = new TestShard();
        TestPagerAdapter newAdapter = new TestPagerAdapter(context, new Shard[]{newShard1, newShard2});
        newViewPager.setAdapter(newAdapter);
        newViewPager.restoreHierarchyState(state);

        showViewPager(newViewPager);
        goToPage(newViewPager, 0);
        goToPage(newViewPager, 1);

        assertEquals(1, ((TestShard) newAdapter.shards[0]).state);
        assertEquals(2, ((TestShard) newAdapter.shards[1]).state);
    }

    private void showViewPager(final ViewPager2 viewPager) throws Throwable {
        activityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().setContentView(viewPager, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    private void removeViewPager(final ViewPager2 viewPager2) throws Throwable {
        activityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) viewPager2.getParent();
                parent.removeView(viewPager2);
            }
        });
    }

    private void goToPage(final ViewPager2 viewPager, final int page) throws Throwable {
        activityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(page, false);
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }
}
