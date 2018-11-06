package me.tatarka.shard.test;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.ViewModel;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;
import me.tatarka.shard.lifecycle.ViewModelProviders;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4.class)
public class ShardViewModelTest {
    ShardManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        fm = new ShardManager(new TestShardOwner(null));
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void canObtainViewModelAfterShardIsCreated() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = ViewModelProviders.of(shard).get(TestViewModel.class);

        assertNotNull(viewModel);
    }

    @Test
    public void viewModelIsRetainedAcrossConfigurationChange() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = ViewModelProviders.of(shard).get(TestViewModel.class);
        Shard.State state = fm.saveState(shard);
        Shard newShard = new Shard();
        fm.restoreState(newShard, state);
        fm.add(newShard, container);
        TestViewModel newViewModel = ViewModelProviders.of(newShard).get(TestViewModel.class);

        assertSame(viewModel, newViewModel);
    }

    @Test
    public void viewModelIsNotRetainedWhenShardIsDestroyed() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = ViewModelProviders.of(shard).get(TestViewModel.class);
        Shard.State state = fm.saveState(shard);
        fm.remove(shard);
        Shard newShard = new Shard();
        fm.restoreState(newShard, state);
        fm.add(newShard, container);
        TestViewModel newViewModel = ViewModelProviders.of(newShard).get(TestViewModel.class);

        assertNotSame(viewModel, newViewModel);
    }

    public static class TestViewModel extends ViewModel {
        boolean onClearedCalled;

        @Override
        protected void onCleared() {
            onClearedCalled = true;
        }
    }
}
