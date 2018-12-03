package me.tatarka.shard.nav;

import android.app.Instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class OptimizingNavigatorTest {

    Instrumentation instrumentation;
    NavController controller;
    TestOptimizingNavigator navigator;
    NavGraph navGraph;

    @Before
    @UiThreadTest
    public void setup() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        controller = new NavController(InstrumentationRegistry.getInstrumentation().getTargetContext());
        navigator = new TestOptimizingNavigator();
        controller.getNavigatorProvider().addNavigator(navigator);
        navGraph = new NavGraph(controller.getNavigatorProvider());
    }

    @Test
    public void initialPage() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, navigator.transactions.size());

                TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                assertEquals("start", transaction.newPage.name);
                assertNull(transaction.oldPage);
                assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);

                assertEquals(0, navigator.savedPages.size());

                assertEquals(0, navigator.restoredStates.size());

                assertEquals(1, navigator.destinations.size());
                assertEquals(1, (int) navigator.destinations.get(0));
            }
        });
    }

    @Test
    public void singlePush() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
                controller.navigate(2);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, navigator.transactions.size());

                TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                assertEquals("one", transaction.newPage.name);
                assertNull(transaction.oldPage);
                assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);

                assertEquals(1, navigator.savedPages.size());
                assertEquals("start", navigator.savedPages.get(0).name);

                assertEquals(0, navigator.restoredStates.size());

                assertEquals(2, navigator.destinations.size());
                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
            }
        });
    }

    @Test
    public void singlePop() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
                controller.navigate(2);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.popBackStack();
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(2, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("one", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("start", transaction.newPage.name);
                    assertEquals("one", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_POPPED, transaction.backStackEffect);
                }

                assertEquals(1, navigator.savedPages.size());
                assertEquals("start", navigator.savedPages.get(0).name);

                assertEquals(1, navigator.restoredStates.size());
                assertEquals("start", navigator.restoredStates.get(0).name);

                assertEquals(3, navigator.destinations.size());
                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(1, (int) navigator.destinations.get(2));
            }
        });
    }

    @Test
    public void doublePush() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.addDestination(navigator.createDestination().id(3).name("two"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(2);
                controller.navigate(3);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(2, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("start", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("two", transaction.newPage.name);
                    assertEquals("start", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }

                assertEquals(2, navigator.savedPages.size());
                assertEquals("start", navigator.savedPages.get(0).name);
                assertEquals("one", navigator.savedPages.get(1).name);

                assertEquals(0, navigator.restoredStates.size());

                assertEquals(3, navigator.destinations.size());
                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(3, (int) navigator.destinations.get(2));
            }
        });
    }

    @Test
    public void doublePop() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.addDestination(navigator.createDestination().id(3).name("two"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
                controller.navigate(2);
                controller.navigate(3);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.popBackStack(1, false);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(2, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("two", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("start", transaction.newPage.name);
                    assertEquals("two", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_POPPED, transaction.backStackEffect);
                }

                assertEquals(2, navigator.savedPages.size());
                assertEquals("start", navigator.savedPages.get(0).name);
                assertEquals("one", navigator.savedPages.get(1).name);

                assertEquals(1, navigator.restoredStates.size());
                assertEquals("start", navigator.restoredStates.get(0).name);

                assertEquals(5, navigator.destinations.size());
                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(3, (int) navigator.destinations.get(2));
                assertEquals(2, (int) navigator.destinations.get(3));
                assertEquals(1, (int) navigator.destinations.get(4));
            }
        });
    }

    @Test
    public void replaceTop() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.addDestination(navigator.createDestination().id(3).name("two"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
                controller.navigate(2);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(3, null, new NavOptions.Builder()
                        .setPopUpTo(1, false)
                        .build());
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(2, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("one", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("two", transaction.newPage.name);
                    assertEquals("one", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }

                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(1, (int) navigator.destinations.get(2));
                assertEquals(3, (int) navigator.destinations.get(3));
            }
        });
    }

    @Test
    public void singleTop() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(2);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(1, null, new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(1, false)
                        .build());
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(3, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("start", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("one", transaction.newPage.name);
                    assertEquals("start", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(2);
                    assertEquals("start", transaction.newPage.name);
                    assertEquals("one", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_POPPED, transaction.backStackEffect);
                }

                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(1, (int) navigator.destinations.get(2));
            }
        });
    }

    @Test
    public void replacePop() {
        await(new Runnable() {
            @Override
            public void run() {
                navGraph.addDestination(navigator.createDestination().id(1).name("start"));
                navGraph.addDestination(navigator.createDestination().id(2).name("one"));
                navGraph.addDestination(navigator.createDestination().id(3).name("two"));
                navGraph.setStartDestination(1);
                controller.setGraph(navGraph);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(2);
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.navigate(3, null, new NavOptions.Builder()
                        .setPopUpTo(1, false)
                        .build());
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                controller.popBackStack();
            }
        });
        await(new Runnable() {
            @Override
            public void run() {
                assertEquals(4, navigator.transactions.size());

                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(0);
                    assertEquals("start", transaction.newPage.name);
                    assertNull(transaction.oldPage);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(1);
                    assertEquals("one", transaction.newPage.name);
                    assertEquals("start", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(2);
                    assertEquals("two", transaction.newPage.name);
                    assertEquals("one", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_ADDED, transaction.backStackEffect);
                }
                {
                    TestOptimizingNavigator.Transaction transaction = navigator.transactions.get(3);
                    assertEquals("start", transaction.newPage.name);
                    assertEquals("two", transaction.oldPage.name);
                    assertEquals(Navigator.BACK_STACK_DESTINATION_POPPED, transaction.backStackEffect);
                }

                assertEquals(1, (int) navigator.destinations.get(0));
                assertEquals(2, (int) navigator.destinations.get(1));
                assertEquals(1, (int) navigator.destinations.get(2));
                assertEquals(3, (int) navigator.destinations.get(3));
                assertEquals(1, (int) navigator.destinations.get(4));
            }
        });
    }

    private void await(Runnable runnable) {
        instrumentation.runOnMainSync(runnable);
        instrumentation.waitForIdleSync();
    }
}
