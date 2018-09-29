package me.tatarka.betterfragment.test;

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
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.app.FragmentActivity;
import me.tatarka.betterfragment.wiget.FragmentHost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FragmentHostTest {

    Instrumentation instrumentation;

    @Rule
    public ActivityTestRule<FragmentHostActivity> activityTestRule = new ActivityTestRule<>(FragmentHostActivity.class);

    @Before
    @UiThreadTest
    public void setup() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
    }

    @Test
    public void setsNamedFragmentOnAttach() {
        await(new Runnable() {
            @Override
            public void run() {
                FragmentHost host = fragmentHost();

                assertEquals(TestFragment.class, host.getFragment().getClass());
            }
        });
    }

    @Test
    public void restoresNamedFragmentAfterConfigChange() {
        await(new Runnable() {
            @Override
            public void run() {
                FragmentHost host = fragmentHost();
                host.getFragment().getInstanceStateStore().add("test", new TestInstanceStateSaver(1));
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
                FragmentHost host = fragmentHost();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver();
                host.getFragment().getInstanceStateStore().add("test", stateSaver);

                assertEquals(TestFragment.class, host.getFragment().getClass());
                assertTrue(stateSaver.restoreStateCalled);
                assertEquals(1, stateSaver.state);
            }
        });
    }

    @Test
    public void replacesFragment() {
        await(new Runnable() {
            @Override
            public void run() {
                FragmentHost host = fragmentHost();
                host.setFragment(new OtherFragment());

                assertEquals(OtherFragment.class, host.getFragment().getClass());
            }
        });
    }

    @Test
    public void keepsReplacedFragmentOnConfigChange() {
        await(new Runnable() {
            @Override
            public void run() {
                FragmentHost host = fragmentHost();
                OtherFragment fragment = new OtherFragment();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver(1);
                fragment.getInstanceStateStore().add("test", stateSaver);
                host.setFragment(fragment);
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
                FragmentHost host = fragmentHost();
                Fragment fragment = host.getFragment();
                TestInstanceStateSaver stateSaver = new TestInstanceStateSaver();
                fragment.getInstanceStateStore().add("test", stateSaver);

                assertEquals(OtherFragment.class, fragment.getClass());
                assertTrue(stateSaver.restoreStateCalled);
                assertEquals(1, stateSaver.state);
            }
        });
    }

    private FragmentHost fragmentHost() {
        return activityTestRule.getActivity().requireViewById(R.id.host);
    }

    private void await(Runnable runnable) {
        instrumentation.runOnMainSync(runnable);
        instrumentation.waitForIdleSync();
    }

    public static class FragmentHostActivity extends FragmentActivity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_host);
        }
    }

    public static class OtherFragment extends TestFragment {
    }
}
