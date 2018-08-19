package me.tatarka.betterfragment.test.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.betterfragment.test.TestActivity;
import me.tatarka.betterfragment.test.TestFragment;
import me.tatarka.betterfragment.test.TestFragmentFactory;
import me.tatarka.betterfragment.widget.FragmentHost;

import me.tatarka.betterfragment.test.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FragmentHostTest {
    Context context;

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(TestActivity.class);

    @Before
    public void setup() {
        context = activityTestRule.getActivity();
    }

    @Test
    public void attachesFragmentInCode() {
        FragmentHost host = new FragmentHost(context);
        TestFragment fragment = new TestFragment();
        host.setFragment(fragment);

        assertTrue(fragment.createCalled);
        assertNotNull(host.getChildAt(0));
    }

    @Test
    public void restoresFragmentInCodeWithFactory() {
        TestFragmentFactory factory = new TestFragmentFactory();
        FragmentHost host = new FragmentHost(context);
        host.setFragmentFactory(factory);
        host.setId(2);
        TestFragment fragment = new TestFragment();
        host.setFragment(fragment);
        SparseArray<Parcelable> state = new SparseArray<>();
        host.saveHierarchyState(state);
        FragmentHost newHost = new FragmentHost(context);
        newHost.setFragmentFactory(factory);
        newHost.setId(2);
        newHost.restoreHierarchyState(state);

        assertTrue(factory.newInstanceCalled);
        assertTrue(fragment.createCalled);
        assertNotNull(host.getChildAt(0));
    }

    @Test
    public void attachesInitialFragmentInLayout() {
        final FragmentHost host = (FragmentHost) LayoutInflater.from(context).inflate(R.layout.fragment_host_test_name, null);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(host, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });

        assertNotNull(host.getFragment());
    }

    @Test
    public void attachesInitialFragmentInLayoutWithFactory() {
        final FragmentHost host = (FragmentHost) LayoutInflater.from(context).inflate(R.layout.fragment_host_test_name, null);
        TestFragmentFactory factory = new TestFragmentFactory();
        host.setFragmentFactory(factory);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().addContentView(host, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });

        assertTrue(factory.newInstanceCalled);
    }

    @Test
    public void savesAndRestoresState() {
        FragmentHost host = new FragmentHost(context);
        host.setId(2);
        TestFragment fragment = new TestFragment();
        fragment.state = 1;
        host.setFragment(fragment);
        SparseArray<Parcelable> state = new SparseArray<>();
        host.saveHierarchyState(state);
        FragmentHost newHost = new FragmentHost(context);
        newHost.setId(2);
        newHost.restoreHierarchyState(state);
        TestFragment newFragment = (TestFragment) newHost.getFragment();

        assertEquals(1, newFragment.state);
    }
}
