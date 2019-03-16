package me.tatarka.shard.nav;

import androidx.navigation.NavOptions;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static me.tatarka.shard.nav.LooperTestHelper.withPausedMainLooper;
import static me.tatarka.shard.nav.OptimizingNavigator.DIRECTION_POP;
import static me.tatarka.shard.nav.OptimizingNavigator.DIRECTION_PUSH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class OptimizingNavigatorTest {

    @Test
    public void initialPage() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.setStartDestination(1);
                    activity.controller.setGraph(activity.navGraph);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(1, activity.navigator.transactions.size());

                    TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                    assertEquals("start", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(DIRECTION_PUSH, transaction.direction);

                    assertEquals(0, activity.navigator.savedPages.size());

                    assertEquals(0, activity.navigator.restoredStates.size());

                    assertEquals(1, activity.navigator.destinations.size());
                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                }
            });
        }
    }

    @Test
    public void singlePush() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.setStartDestination(1);
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.setGraph(activity.navGraph);
                            activity.controller.navigate(2);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(1, activity.navigator.transactions.size());

                    TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                    assertEquals("one", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(DIRECTION_PUSH, transaction.direction);

                    assertEquals(1, activity.navigator.savedPages.size());
                    assertEquals("start", activity.navigator.savedPages.get(0).name);

                    assertEquals(0, activity.navigator.restoredStates.size());

                    assertEquals(2, activity.navigator.destinations.size());
                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                }
            });
        }
    }

    @Test
    public void singlePop() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.setStartDestination(1);
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.setGraph(activity.navGraph);
                            activity.controller.navigate(2);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.controller.popBackStack();
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(2, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("one", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("start", transaction.newPage.name);
                        assertEquals("one", transaction.oldPage.name);
                        assertEquals(DIRECTION_POP, transaction.direction);
                    }

                    assertEquals(1, activity.navigator.savedPages.size());
                    assertEquals("start", activity.navigator.savedPages.get(0).name);

                    assertEquals(1, activity.navigator.restoredStates.size());
                    assertEquals("start", activity.navigator.restoredStates.get(0).name);

                    assertEquals(3, activity.navigator.destinations.size());
                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(1, (int) activity.navigator.destinations.get(2));
                }
            });
        }
    }

    @Test
    public void doublePush() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(3).name("two"));
                    activity.navGraph.setStartDestination(1);
                    activity.controller.setGraph(activity.navGraph);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.navigate(2);
                            activity.controller.navigate(3);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(2, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("start", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("two", transaction.newPage.name);
                        assertEquals("start", transaction.oldPage.name);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }

                    assertEquals(2, activity.navigator.savedPages.size());
                    assertEquals("start", activity.navigator.savedPages.get(0).name);
                    assertEquals("one", activity.navigator.savedPages.get(1).name);

                    assertEquals(0, activity.navigator.restoredStates.size());

                    assertEquals(3, activity.navigator.destinations.size());
                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(3, (int) activity.navigator.destinations.get(2));
                }
            });
        }
    }

    @Test
    public void doublePop() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(3).name("two"));
                    activity.navGraph.setStartDestination(1);
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.setGraph(activity.navGraph);
                            activity.controller.navigate(2);
                            activity.controller.navigate(3);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.popBackStack(1, false);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(2, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("two", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("start", transaction.newPage.name);
                        assertEquals("two", transaction.oldPage.name);
                        assertEquals(DIRECTION_POP, transaction.direction);
                    }

                    assertEquals(2, activity.navigator.savedPages.size());
                    assertEquals("start", activity.navigator.savedPages.get(0).name);
                    assertEquals("one", activity.navigator.savedPages.get(1).name);

                    assertEquals(1, activity.navigator.restoredStates.size());
                    assertEquals("start", activity.navigator.restoredStates.get(0).name);

                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(3, (int) activity.navigator.destinations.get(2));
                    assertEquals(1, (int) activity.navigator.destinations.get(3));
                }
            });
        }
    }

    @Test
    public void replaceTop() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(3).name("two"));
                    activity.navGraph.setStartDestination(1);
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.setGraph(activity.navGraph);
                            activity.controller.navigate(2);
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.navigate(3, null, new NavOptions.Builder()
                                    .setPopUpTo(1, false)
                                    .build());
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(2, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("one", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("two", transaction.newPage.name);
                        assertEquals("one", transaction.oldPage.name);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }

                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(3, (int) activity.navigator.destinations.get(2));
                }
            });
        }
    }

    @Test
    public void singleTop() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.setStartDestination(1);
                    activity.controller.setGraph(activity.navGraph);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.controller.navigate(2);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.navigate(1, null, new NavOptions.Builder()
                                    .setLaunchSingleTop(true)
                                    .setPopUpTo(1, false)
                                    .build());
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(3, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("start", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("one", transaction.newPage.name);
                        assertEquals("start", transaction.oldPage.name);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(2);
                        assertEquals("start", transaction.newPage.name);
                        assertEquals("one", transaction.oldPage.name);
                        assertEquals(DIRECTION_POP, transaction.direction);
                    }

                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(1, (int) activity.navigator.destinations.get(2));
                }
            });
        }
    }

    @Test
    public void replacePop() {
        try (ActivityScenario<TestNavActivity> scenario = ActivityScenario.launch(TestNavActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(1).name("start"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(2).name("one"));
                    activity.navGraph.addDestination(activity.navigator.createDestination().id(3).name("two"));
                    activity.navGraph.setStartDestination(1);
                    activity.controller.setGraph(activity.navGraph);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.controller.navigate(2);
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(final TestNavActivity activity) {
                    withPausedMainLooper(new Runnable() {
                        @Override
                        public void run() {
                            activity.controller.navigate(3, null, new NavOptions.Builder()
                                    .setPopUpTo(1, false)
                                    .build());
                        }
                    });
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    activity.controller.popBackStack();
                }
            });
            scenario.onActivity(new ActivityScenario.ActivityAction<TestNavActivity>() {
                @Override
                public void perform(TestNavActivity activity) {
                    assertEquals(4, activity.navigator.transactions.size());

                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(0);
                        assertEquals("start", transaction.newPage.name);
                        assertNull(transaction.oldPage);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(1);
                        assertEquals("one", transaction.newPage.name);
                        assertEquals("start", transaction.oldPage.name);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(2);
                        assertEquals("two", transaction.newPage.name);
                        assertEquals("one", transaction.oldPage.name);
                        assertEquals(DIRECTION_PUSH, transaction.direction);
                    }
                    {
                        TestOptimizingNavigator.Transaction transaction = activity.navigator.transactions.get(3);
                        assertEquals("start", transaction.newPage.name);
                        assertEquals("two", transaction.oldPage.name);
                        assertEquals(DIRECTION_POP, transaction.direction);
                    }

                    assertEquals(1, (int) activity.navigator.destinations.get(0));
                    assertEquals(2, (int) activity.navigator.destinations.get(1));
                    assertEquals(3, (int) activity.navigator.destinations.get(2));
                    assertEquals(1, (int) activity.navigator.destinations.get(3));
                }
            });
        }
    }
}
