package me.tatarka.shard.fragment.app.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardFragmentTest {

    @Test
    public void triggers_correct_shard_lifecycle() {
        FragmentScenario<TestShardFragment> scenario = FragmentScenario.launchInContainer(TestShardFragment.class);
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                TestShard shard = fragment.getShard();
                assertTrue(shard.onCreateCalled);
                assertEquals(Lifecycle.State.RESUMED, shard.getLifecycle().getCurrentState());
            }
        });
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                TestShard shard = fragment.getShard();
                assertEquals(Lifecycle.State.STARTED, shard.getLifecycle().getCurrentState());
            }
        });
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                TestShard shard = fragment.getShard();
                assertEquals(Lifecycle.State.CREATED, shard.getLifecycle().getCurrentState());
            }
        });
        // We need to get a reference to the shard before destroying since the fragment will be gone afterwards
        final TestShard[] shard = new TestShard[1];
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                shard[0] = fragment.getShard();
            }
        });
        scenario.moveToState(Lifecycle.State.DESTROYED);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                assertEquals(Lifecycle.State.DESTROYED, shard[0].getLifecycle().getCurrentState());
            }
        });
    }

    @Test
    public void restores_state() {
        FragmentScenario<TestShardFragment> scenario = FragmentScenario.launchInContainer(TestShardFragment.class);
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                TestShard shard = fragment.getShard();
                shard.state = 2;
            }
        });
        scenario.recreate();
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                TestShard shard = fragment.getShard();
                assertEquals(2, shard.state);
            }
        });
    }

    @Test
    public void dispatches_start_activity_for_result() {
        FragmentScenario<TestShardFragment> scenario = FragmentScenario.launchInContainer(TestShardFragment.class);
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                fragment.getShard().startActivityForResult();
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        scenario.onFragment(new FragmentScenario.FragmentAction<TestShardFragment>() {
            @Override
            public void perform(@NonNull TestShardFragment fragment) {
                assertEquals(Activity.RESULT_OK, fragment.getShard().resultCode);
            }
        });
    }

    public static class ResultActivity extends Activity {
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }
}
