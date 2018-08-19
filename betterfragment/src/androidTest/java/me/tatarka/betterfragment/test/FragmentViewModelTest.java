package me.tatarka.betterfragment.test;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.ViewModel;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentManager;
import me.tatarka.betterfragment.ViewModelProviders;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

@RunWith(AndroidJUnit4.class)
public class FragmentViewModelTest {
    FragmentManager fm;
    ViewGroup container;

    @Before
    public void setup() {
        fm = new FragmentManager(new TestFragmentOwner());
        container = new FrameLayout(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void canObtainViewModelAfterFragmentIsCreated() {
        Fragment fragment = new Fragment();
        fm.create(fragment, container);
        TestViewModel viewModel = ViewModelProviders.of(fragment).get(TestViewModel.class);

        assertNotNull(viewModel);
    }

    @Test
    public void viewModelIsRetainedAcrossConfigurationChange() {
        Fragment fragment = new Fragment();
        fm.create(fragment, container);
        TestViewModel viewModel = ViewModelProviders.of(fragment).get(TestViewModel.class);
        Fragment.State state = fm.saveState(fragment);
        Fragment newFragment = new Fragment();
        fm.create(newFragment, container, state);
        TestViewModel newViewModel = ViewModelProviders.of(newFragment).get(TestViewModel.class);

        assertSame(viewModel, newViewModel);
    }

    @Test
    public void viewModelIsNotRetainedWhenFragmentIsDestroyed() {
        Fragment fragment = new Fragment();
        fm.create(fragment, container);
        TestViewModel viewModel = ViewModelProviders.of(fragment).get(TestViewModel.class);
        Fragment.State state = fm.saveState(fragment);
        fm.destroy(fragment);
        Fragment newFragment = new Fragment();
        fm.create(newFragment, container, state);
        TestViewModel newViewModel = ViewModelProviders.of(newFragment).get(TestViewModel.class);

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
