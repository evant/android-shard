package me.tatarka.shard.pager.test;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager.widget.ViewPager;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardActivity;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShardPagerAdapterTest {

    public static class CreatesPagesActivity extends ShardActivity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final ViewPager viewPager = new ViewPager(this);
            viewPager.setId(2);
            TestPagerAdapter adapter = new TestPagerAdapter(this, new Shard[]{new TestShard(), new TestShard(), new TestShard()});
            viewPager.setAdapter(adapter);
            setContentView(viewPager);
        }
    }

    @Test
    public void createsPages() {
        try (ActivityScenario<CreatesPagesActivity> scenario = ActivityScenario.launch(CreatesPagesActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<CreatesPagesActivity>() {
                @Override
                public void perform(CreatesPagesActivity activity) {
                    TestPagerAdapter adapter = (TestPagerAdapter) ((ViewPager) activity.findViewById(2)).getAdapter();
                    assertEquals(2, adapter.getItemCalledCount);
                }
            });
        }
    }

    public static class SavesAndRestoresStateOnConfigChangeActivity extends ShardActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final ViewPager viewPager = new ViewPager(this);
            viewPager.setId(2);
            TestShard shard1 = new TestShard();
            TestShard shard2 = new TestShard();
            if (savedInstanceState == null) {
                shard1.state = 1;
                shard2.state = 2;
            }
            TestPagerAdapter adapter = new TestPagerAdapter(this, new Shard[]{shard1, shard2});
            viewPager.setAdapter(adapter);
            setContentView(viewPager);
        }
    }

    @Test
    public void savesAndRestoresStateOnConfigChange() {
        try (ActivityScenario<SavesAndRestoresStateOnConfigChangeActivity> scenario = ActivityScenario.launch(SavesAndRestoresStateOnConfigChangeActivity.class)) {
            scenario.recreate();
            scenario.onActivity(new ActivityScenario.ActivityAction<SavesAndRestoresStateOnConfigChangeActivity>() {
                @Override
                public void perform(SavesAndRestoresStateOnConfigChangeActivity activity) {
                    TestPagerAdapter newAdapter = (TestPagerAdapter) ((ViewPager) activity.findViewById(2)).getAdapter();

                    assertEquals(1, ((TestShard) newAdapter.shards[0]).state);
                    assertEquals(2, ((TestShard) newAdapter.shards[1]).state);
                }
            });
        }
    }
}
