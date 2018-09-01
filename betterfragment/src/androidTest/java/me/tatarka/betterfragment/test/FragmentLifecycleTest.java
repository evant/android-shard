package me.tatarka.betterfragment.test;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FragmentLifecycleTest {

    LifecycleRegistry registry;
    FragmentManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        TestFragmentOwner owner = new TestFragmentOwner();
        registry = owner.lifecycleRegistry;
        fm = new FragmentManager(owner);
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void createCallsOnCreate() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE}, observer.getEvents());
    }

    @Test
    public void destroyCallsOnDestroy() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        fm.remove(fragment);

        assertEquals(Lifecycle.State.DESTROYED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_DESTROY}, observer.getEvents());
    }


    @Test
    public void saveStateCallsOnStop() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        fm.saveState(fragment);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_STOP}, observer.getEvents());
    }

    @Test
    public void startOnOwnerCallsStartOnFragment() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        assertEquals(Lifecycle.State.STARTED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START}, observer.getEvents());
    }

    @Test
    public void resumeOnOwnerCallsResumeOnFragment() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        assertEquals(Lifecycle.State.RESUMED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME}, observer.getEvents());
    }

    @Test
    public void pauseOnOwnerCallsPauseOnFragment() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);

        assertEquals(Lifecycle.State.STARTED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_PAUSE}, observer.getEvents());
    }

    @Test
    public void stopOnOwnerCallsStopOnFragment() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);

        assertEquals(Lifecycle.State.CREATED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_STOP}, observer.getEvents());
    }

    @Test
    public void destroyOnOwnerCallsDestroyOnFragment() {
        Fragment fragment = new Fragment();
        TestLifecycleObserver observer = new TestLifecycleObserver();
        fragment.getLifecycle().addObserver(observer);
        fm.add(fragment, container);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);

        assertEquals(Lifecycle.State.DESTROYED, observer.getCurrentState());
        assertArrayEquals(new Lifecycle.Event[]{Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_DESTROY}, observer.getEvents());
    }
}
