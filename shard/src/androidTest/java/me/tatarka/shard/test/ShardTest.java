package me.tatarka.shard.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardTest {

    ShardManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        fm = new ShardManager(new TestShardOwner());
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void createCallsOnCreate() {
        TestShard shard = new TestShard();
        fm.add(shard, container);

        assertTrue(shard.createCalled);
    }

    @Test
    public void createAddsViewToContainer() {
        TestShard shard = new TestShard();
        fm.add(shard, container);

        assertNotNull(container.getChildAt(0));
    }

    @Test
    public void saveStateCallsOnSaveInstanceState() {
        TestShard shard = new TestShard();
        TestInstanceStateSaver stateSaver = new TestInstanceStateSaver();
        shard.getInstanceStateStore().add("test", stateSaver);
        fm.add(shard, container);
        fm.saveState(shard);

        assertTrue(stateSaver.saveStateCalled);
    }

    @Test
    public void setContentViewAddsToShardView() {
        TestShard shard = new TestShard();
        fm.add(shard, container);
        View view = new View(container.getContext());
        shard.setContentView(view);

        Assert.assertEquals(view, shard.getView().getChildAt(0));
    }

    @Test
    public void destroyRemovesContentViewFromContainer() {
        TestShard shard = new TestShard();
        fm.add(shard, container);
        View view = new View(container.getContext());
        shard.setContentView(view);
        fm.remove(shard);

        assertNull(container.getChildAt(0));
    }

    @Test
    public void findViewByIdFindsShardView() {
        TestShard shard = new TestShard();
        fm.add(shard, container);
        View view = new View(container.getContext());
        view.setId(2);
        shard.setContentView(view);

        Assert.assertEquals(view, shard.findViewById(2));
    }

    @Test
    public void requireViewByIdFindsShardView() {
        TestShard shard = new TestShard();
        fm.add(shard, container);
        View view = new View(container.getContext());
        view.setId(2);
        shard.setContentView(view);

        Assert.assertEquals(view, shard.requireViewById(2));
    }

    @Test
    public void shardSavesAndRestoresState() {
        TestShard shard = new TestShard();
        TestInstanceStateSaver stateSaver = new TestInstanceStateSaver(1);
        shard.getInstanceStateStore().add("test", stateSaver);
        fm.add(shard, container);
        Shard.State state = fm.saveState(shard);
        TestShard newShard = new TestShard();
        TestInstanceStateSaver newStateSaver = new TestInstanceStateSaver();
        newShard.getInstanceStateStore().add("test", newStateSaver);
        fm.restoreState(newShard, state);
        fm.add(newShard, container);

        assertTrue(stateSaver.saveStateCalled);
        assertTrue(newStateSaver.restoreStateCalled);
        assertEquals(1, newStateSaver.state);
    }

}
