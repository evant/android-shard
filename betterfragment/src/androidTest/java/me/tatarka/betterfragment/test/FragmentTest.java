package me.tatarka.betterfragment.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FragmentTest {

    FragmentManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        fm = new FragmentManager(new TestFragmentOwner());
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void createCallsOnCreate() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);

        assertTrue(fragment.createCalled);
    }

    @Test
    public void createAddsViewToContainer() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);

        assertNotNull(container.getChildAt(0));
    }

    @Test
    public void saveStateCallsOnSaveInstanceState() {
        TestFragment fragment = new TestFragment();
        TestStateSaver stateSaver = new TestStateSaver();
        fragment.getStateStore().addStateSaver("test", stateSaver);
        fm.add(fragment, container);
        fm.saveState(fragment);

        assertTrue(stateSaver.saveStateCalled);
    }

    @Test
    public void setContentViewAddsToFragmentView() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);
        View view = new View(container.getContext());
        fragment.setContentView(view);

        Assert.assertEquals(view, fragment.getView().getChildAt(0));
    }

    @Test
    public void destroyRemovesContentViewFromContainer() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);
        View view = new View(container.getContext());
        fragment.setContentView(view);
        fm.remove(fragment);

        assertNull(container.getChildAt(0));
    }

    @Test
    public void findViewByIdFindsFragmentView() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);
        View view = new View(container.getContext());
        view.setId(2);
        fragment.setContentView(view);

        Assert.assertEquals(view, fragment.findViewById(2));
    }

    @Test
    public void requireViewByIdFindsFragmentView() {
        TestFragment fragment = new TestFragment();
        fm.add(fragment, container);
        View view = new View(container.getContext());
        view.setId(2);
        fragment.setContentView(view);

        Assert.assertEquals(view, fragment.requireViewById(2));
    }

    @Test
    public void fragmentSavesAndRestoresState() {
        TestFragment fragment = new TestFragment();
        TestStateSaver stateSaver = new TestStateSaver(1);
        fragment.getStateStore().addStateSaver("test", stateSaver);
        fm.add(fragment, container);
        Fragment.State state = fm.saveState(fragment);
        TestFragment newFragment = new TestFragment();
        TestStateSaver newStateSaver = new TestStateSaver();
        newFragment.getStateStore().addStateSaver("test", newStateSaver);
        fm.restoreState(newFragment, state);
        fm.add(newFragment, container);

        assertTrue(stateSaver.saveStateCalled);
        assertTrue(newStateSaver.restoreStateCalled);
        assertEquals(1, newStateSaver.state);
    }

}
