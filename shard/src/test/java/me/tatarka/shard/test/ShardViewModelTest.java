package me.tatarka.shard.test;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;

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
        container = new FrameLayout(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void canObtainViewModelAfterShardIsCreated() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = new ViewModelProvider(shard).get(TestViewModel.class);

        assertNotNull(viewModel);
    }

    @Test
    public void viewModelIsRetainedAcrossConfigurationChange() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = new ViewModelProvider(shard).get(TestViewModel.class);
        Shard.State state = fm.saveState(shard);
        Shard newShard = new Shard();
        fm.restoreState(newShard, state);
        fm.add(newShard, container);
        TestViewModel newViewModel = new ViewModelProvider(newShard).get(TestViewModel.class);

        assertSame(viewModel, newViewModel);
    }

    @Test
    public void viewModelIsNotRetainedWhenShardIsDestroyed() {
        Shard shard = new Shard();
        fm.add(shard, container);
        TestViewModel viewModel = new ViewModelProvider(shard).get(TestViewModel.class);
        Shard.State state = fm.saveState(shard);
        fm.remove(shard);
        Shard newShard = new Shard();
        fm.restoreState(newShard, state);
        fm.add(newShard, container);
        TestViewModel newViewModel = new ViewModelProvider(newShard).get(TestViewModel.class);

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
