package me.tatarka.shard.test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardActivity;
import me.tatarka.shard.wiget.ShardHost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardHostTest {

    @Test
    public void setsNamedShardOnAttach() {
        try (ActivityScenario<ShardHostActivity> scenario = ActivityScenario.launch(ShardHostActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);

                    assertEquals(TestShard.class, host.getShard().getClass());
                }
            });
        }
    }

    @Test
    public void restoresNamedShardAfterConfigChange() {
        try (ActivityScenario<ShardHostActivity> scenario = ActivityScenario.launch(ShardHostActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);
                    new TestInstanceStateSaver("test", 1, host.getShard());
                }
            });
            scenario.recreate();
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);
                    TestInstanceStateSaver stateSaver = new TestInstanceStateSaver("test", host.getShard());

                    assertEquals(TestShard.class, host.getShard().getClass());
                    assertTrue(stateSaver.restoreStateCalled);
                    assertEquals(1, stateSaver.state);
                }
            });
        }
    }

    @Test
    public void replacesShard() {
        try (ActivityScenario<ShardHostActivity> scenario = ActivityScenario.launch(ShardHostActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);
                    host.setShard(new OtherShard());

                    assertEquals(OtherShard.class, host.getShard().getClass());
                }
            });
        }
    }

    @Test
    public void keepsReplacedShardOnConfigChange() {
        try (ActivityScenario<ShardHostActivity> scenario = ActivityScenario.launch(ShardHostActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);
                    OtherShard shard = new OtherShard();
                    host.setShard(shard);
                    new TestInstanceStateSaver("test", 1, shard);
                }
            });
            scenario.recreate();
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardHostActivity>() {
                @Override
                public void perform(ShardHostActivity activity) {
                    ShardHost host = shardHost(activity);
                    Shard shard = host.getShard();
                    TestInstanceStateSaver stateSaver = new TestInstanceStateSaver("test", shard);

                    assertEquals(OtherShard.class, shard.getClass());
                    assertTrue(stateSaver.restoreStateCalled);
                    assertEquals(1, stateSaver.state);
                }
            });
        }
    }

    private static ShardHost shardHost(ShardHostActivity activity) {
        return activity.requireViewById(R.id.host);
    }

    public static class ShardHostActivity extends ShardActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.shard_host);
        }
    }

    public static class OtherShard extends TestShard {
    }
}
