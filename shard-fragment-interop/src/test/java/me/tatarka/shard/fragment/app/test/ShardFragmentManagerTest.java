package me.tatarka.shard.fragment.app.test;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.fragment.test.R;

import static me.tatarka.shard.fragment.app.ShardFragmentManager.getFragmentManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardFragmentManagerTest {

    @Test
    public void triggers_correct_fragment_lifecycle() {
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    TestShard shard = activity.getShard();
                    assertTrue(shard.fragment.onCreateCalled);
                    assertTrue(shard.fragment.onCreateViewCalled);
                    assertTrue(shard.fragment.onActivityCreatedCalled);
                    assertEquals(Lifecycle.State.RESUMED, shard.fragment.getLifecycle().getCurrentState());
                }
            });
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    TestShard shard = activity.getShard();
                    assertEquals(Lifecycle.State.STARTED, shard.fragment.getLifecycle().getCurrentState());
                }
            });
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    TestShard shard = activity.getShard();
                    assertEquals(Lifecycle.State.CREATED, shard.fragment.getLifecycle().getCurrentState());
                }
            });
            // We need to get a reference to the shard before destroying since the fragment will be gone afterwards
            final TestShard[] shard = new TestShard[1];
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    shard[0] = activity.getShard();
                }
            });
            scenario.moveToState(Lifecycle.State.DESTROYED);
            InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    assertTrue(shard[0].fragment.onDestroyCalled);
                    // Fragments move back into the initialized state when they are destroyed so they can be reused.
                    assertEquals(Lifecycle.State.INITIALIZED, shard[0].fragment.getLifecycle().getCurrentState());
                }
            });
        }
    }

    @Test
    public void restores_state() {
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    activity.getShard().fragment.state = 2;
                }
            });
            scenario.recreate();
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    assertEquals(2, activity.getShard().fragment.state);
                }
            });
        }
    }

    @Test
    public void dispatches_start_activity_for_result() {
        InstrumentationRegistry.getInstrumentation().addMonitor(ResultActivity.class.getName(), new Instrumentation.ActivityResult(Activity.RESULT_OK, null), true);
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    activity.getShard().fragment.startActivityForResult(ResultActivity.class);
                }
            });
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    assertEquals(Activity.RESULT_OK, activity.getShard().fragment.resultCode);
                }
            });
        }
    }

    @Test
    public void inflates_fragment_from_layout() {
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestActivity>() {
                @Override
                public void perform(TestActivity activity) {
                    FragmentManager fm = getFragmentManager(activity.getShard());
                    assertNotNull(fm.findFragmentById(R.id.child_fragment));
                }
            });
        }
    }
}
