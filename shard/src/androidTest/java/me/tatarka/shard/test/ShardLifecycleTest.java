package me.tatarka.shard.test;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.app.ShardManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShardLifecycleTest {

    LifecycleRegistry registry;
    ShardManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        TestShardOwner owner = new TestShardOwner(null);
        registry = owner.lifecycleRegistry;
        fm = new ShardManager(owner);
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void createCallsOnCreate() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE}, observer.getEvents());
    }

    @Test
    public void destroyCallsOnDestroy() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        fm.remove(shard);

        assertEquals(Lifecycle.State.DESTROYED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_DESTROY}, observer.getEvents());
    }


    @Test
    public void saveStateCallsOnStop() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        fm.saveState(shard);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_STOP}, observer.getEvents());
    }

    @Test
    public void startOnOwnerCallsStartOnShard() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        assertEquals(Lifecycle.State.STARTED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START}, observer.getEvents());
    }

    @Test
    public void resumeOnOwnerCallsResumeOnShard() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        assertEquals(Lifecycle.State.RESUMED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME}, observer.getEvents());
    }

    @Test
    public void pauseOnOwnerCallsPauseOnShard() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);

        assertEquals(Lifecycle.State.STARTED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE}, observer.getEvents());
    }

    @Test
    public void stopOnOwnerCallsStopOnShard() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_STOP}, observer.getEvents());
    }

    @Test
    public void destroyOnOwnerCallsDestroyOnShard() {
        Shard shard = new Shard();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        shard.getLifecycle().addObserver(observer);
        fm.add(shard, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        assertEquals(Lifecycle.State.DESTROYED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_DESTROY}, observer.getEvents());
    }
}
