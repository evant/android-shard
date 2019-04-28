package me.tatarka.shard.backstack.test;

import android.widget.FrameLayout;

import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.backstack.NavShardTransition;
import me.tatarka.shard.backstack.ShardBackStack;

import static me.tatarka.shard.backstack.test.LooperTestHelper.withPausedMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardBackStackTest {

    private final TestShardOwner owner = new TestShardOwner();
    private final FrameLayout container = new FrameLayout(owner.getContext());
    private ShardBackStack backStack;

    @Before
    @UiThreadTest
    public void setup() {
        backStack = new ShardBackStack(owner, container);
    }

    @Test
    @UiThreadTest
    public void starting_shard() {
        TestShard shard = new TestShard();
        backStack.setStarting(shard).commit();

        assertEquals(0, backStack.size());
        assertTrue(shard.createCalled);
    }

    @Test
    @UiThreadTest
    public void push_and_pop() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).push(shard2).commit();
            }
        });

        assertEquals(1, backStack.size());
        assertFalse(shard1.createCalled);
        assertTrue(shard2.createCalled);

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().commit();
            }
        });

        assertEquals(0, backStack.size());
        TestShard shard = getShard(0);
        assertTrue(shard.createCalled);
        assertEquals("one", shard.getName());
    }

    @Test
    @UiThreadTest
    public void double_push() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(shard2).push(shard3).commit();
            }
        });

        assertEquals(2, backStack.size());
        assertTrue(shard1.createCalled);
        assertFalse(shard2.createCalled);
        assertTrue(shard3.createCalled);
    }

    @Test
    @UiThreadTest
    public void double_pop() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).push(shard2).push(shard3).commit();
            }
        });

        assertEquals(2, backStack.size());
        assertFalse(shard1.createCalled);
        assertFalse(shard2.createCalled);
        assertTrue(shard3.createCalled);

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().pop().commit();
            }
        });

        assertEquals(0, backStack.size());
        TestShard shard = getShard(0);
        assertTrue(shard.createCalled);
        assertEquals("one", shard.getName());
    }

    @Test
    @UiThreadTest
    public void replace_top() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).push(shard2).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().push(shard3).commit();
            }
        });

        assertEquals(1, backStack.size());
        assertFalse(shard1.createCalled);
        assertTrue(shard2.createCalled);
        assertTrue(shard3.createCalled);
    }

    @Test
    @UiThreadTest
    public void pop_and_push_single_top() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1, 1).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(shard2).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop()
                        .push(TestShard.create("one"), 1, true)
                        .commit();
            }
        });

        assertEquals(0, backStack.size());
    }

    @Test
    @UiThreadTest
    public void pop_and_replace() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(shard2).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().push(shard3).commit();
            }
        });

        assertEquals(1, backStack.size());
    }

    @Test
    @UiThreadTest
    public void pops_to_index_0_exclusive() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).push(shard2).push(shard3).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToIndex(0, false).commit();
            }
        });

        assertEquals(0, backStack.size());
    }

    @Test
    @UiThreadTest
    public void pops_to_index_1_inclusive() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1).push(shard2).push(shard3).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToIndex(1, true).commit();
            }
        });

        assertEquals(0, backStack.size());
    }

    @Test
    @UiThreadTest
    public void pops_to_first_id_exclusive() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1, 1).push(shard2).push(shard3).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToId(1, false).commit();
            }
        });

        assertEquals(0, backStack.size());
    }

    @Test
    @UiThreadTest
    public void pops_to_second_id_inclusive() {
        final TestShard shard1 = TestShard.create("one");
        final TestShard shard2 = TestShard.create("two");
        final TestShard shard3 = TestShard.create("three");

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(shard1, 1).push(shard2, 2).push(shard3).commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToId(2, true).commit();
            }
        });

        assertEquals(0, backStack.size());
    }

    private TestShard getShard(int index) {
        return (TestShard) owner.shardFactory.createdShards.get(index);
    }

}
