package test.me.tatarka.shard.widget;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShardBackStackHostTest {

    @Test
    public void back_with_initial_shard_finishes_the_activity() {
        try (ActivityScenario<ShardBackStackActivity> scenario = ActivityScenario.launch(ShardBackStackActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardBackStackActivity>() {
                @Override
                public void perform(ShardBackStackActivity activity) {
                    activity.host.getBackStack().setStarting(new Shard()).commit();
                    activity.onBackPressed();
                }
            });
            // Checks if activity is finishing
            scenario.getResult();
        }
    }

    @Test
    public void back_with_pushed_shard_pops_it() {
        try (ActivityScenario<ShardBackStackActivity> scenario = ActivityScenario.launch(ShardBackStackActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardBackStackActivity>() {
                @Override
                public void perform(ShardBackStackActivity activity) {
                    activity.host.getBackStack()
                            .setStarting(new Shard())
                            .push(new Shard())
                            .commit();
                    activity.onBackPressed();

                    assertEquals(0, activity.host.getBackStack().size());
                }
            });
        }
    }
}
