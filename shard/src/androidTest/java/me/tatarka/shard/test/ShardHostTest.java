package me.tatarka.shard.test;

import android.app.Instrumentation;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardActivity;
import me.tatarka.shard.wiget.ShardHost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardHostTest {

    Instrumentation instrumentation;

    @Rule
    public ActivityTestRule<ShardHostActivity> activityTestRule = new ActivityTestRule<>(ShardHostActivity.class);

    @Before
    @UiThreadTest
    public void setup() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @Test
    public void setsNamedShardOnAttach() {
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();

                assertEquals(TestShard.class, host.getShard().getClass());
            }
        });
    }

    @Test
    public void restoresNamedShardAfterConfigChange() {
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();
                host.getShard().getSavedStateRegistry().registerSavedStateProvider("test", new TestInstanceStateSaver(1));
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().recreate();
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver();
                host.getShard().getSavedStateRegistry().registerSavedStateProvider("test", stateSaver);

                assertEquals(TestShard.class, host.getShard().getClass());
                assertTrue(stateSaver.restoreStateCalled);
                assertEquals(1, stateSaver.state);
            }
        });
    }

    @Test
    public void replacesShard() {
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();
                host.setShard(new OtherShard());

                assertEquals(OtherShard.class, host.getShard().getClass());
            }
        });
    }

    @Test
    public void keepsReplacedShardOnConfigChange() {
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();
                OtherShard shard = new OtherShard();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver(1);
                shard.getSavedStateRegistry().registerSavedStateProvider("test", stateSaver);
                host.setShard(shard);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().recreate();
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                ShardHost host = shardHost();
                Shard shard = host.getShard();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver();
                shard.getSavedStateRegistry().registerSavedStateProvider("test", stateSaver);

                assertEquals(OtherShard.class, shard.getClass());
                assertTrue(stateSaver.restoreStateCalled);
                assertEquals(1, stateSaver.state);
            }
        });
    }

    private ShardHost shardHost() {
        return activityTestRule.getActivity().requireViewById(R.id.host);
    }

    private void await(Runnable runnable) {
        instrumentation.runOnMainSync(runnable);
        instrumentation.waitForIdleSync();
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
