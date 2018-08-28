package me.tatarka.betterfragment.nav;

import android.app.Instrumentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.runner.AndroidJUnit4;

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
        controller = new NavController(InstrumentationRegistry.getTargetContext());
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
            }
        });
    }

    private void await(Runnable runnable) {
        instrumentation.runOnMainSync(runnable);
        instrumentation.waitForIdleSync();
    }
}
