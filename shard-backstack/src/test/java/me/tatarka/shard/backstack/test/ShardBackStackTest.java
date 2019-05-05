package me.tatarka.shard.backstack.test;

import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.backstack.ShardBackStack;

import static me.tatarka.shard.backstack.test.LooperTestHelper.withPausedMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShardBackStackTest {

    private final TestOnNavigatedListener listener = new TestOnNavigatedListener();
    private ShardBackStack backStack;

    @Before
    @UiThreadTest
    public void setup() {
        TestShardOwner owner = new TestShardOwner();
        backStack = new ShardBackStack(owner, new FrameLayout(owner.getContext()));
        backStack.addOnNavigatedListener(listener);
    }

    @Test
    @UiThreadTest
    public void starting_shard() {
        TestShard shard = new TestShard();
        backStack.setStarting(shard).commit();

        assertEquals(0, backStack.size());
        assertEquals(listener.shards.size(), 1);
        assertEquals(shard, listener.shards.get(0));
    }

    @Test
    @UiThreadTest
    public void push_and_pop() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"))
                        .push(TestShard.create("two"))
                        .commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("two", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void double_push() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one")).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(TestShard.create("two")).push(TestShard.create("three")).commit();
            }
        });

        assertEquals(2, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("one", listener.shards.get(0).getName());
        assertEquals("three", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void double_pop() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"))
                        .push(TestShard.create("two"))
                        .push(TestShard.create("three"))
                        .commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().pop().commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("three", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void replace_top() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"))
                        .push(TestShard.create("two"))
                        .commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().push(TestShard.create("three")).commit();
            }
        });

        assertEquals(1, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("two", listener.shards.get(0).getName());
        assertEquals("three", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void pop_and_push_single_top() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"), 1).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(TestShard.create("two")).commit();
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
        assertEquals(3, listener.shards.size());
        assertEquals("one", listener.shards.get(0).getName());
        assertEquals(1, (int) listener.ids.get(0));
        assertEquals("two", listener.shards.get(1).getName());
        assertEquals("one", listener.shards.get(2).getName());
        assertEquals(1, (int) listener.ids.get(2));
    }

    @Test
    @UiThreadTest
    public void pop_and_replace() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one")).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.push(TestShard.create("two")).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().push(TestShard.create("three")).commit();
            }
        });

        assertEquals(1, backStack.size());
        assertEquals(3, listener.shards.size());
        assertEquals("one", listener.shards.get(0).getName());
        assertEquals("two", listener.shards.get(1).getName());
        assertEquals("three", listener.shards.get(2).getName());
    }

    @Test
    @UiThreadTest
    public void pops_to_index_0_exclusive() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"))
                        .push(TestShard.create("two"))
                        .push(TestShard.create("three")).commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToIndex(0, false).commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("three", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void pops_to_index_1_inclusive() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"))
                        .push(TestShard.create("two"))
                        .push(TestShard.create("three"))
                        .commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToIndex(1, true).commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("three", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
    }

    @Test
    @UiThreadTest
    public void pops_to_first_id_exclusive() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"), 1)
                        .push(TestShard.create("two"))
                        .push(TestShard.create("three"))
                        .commit();
            }
        });
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToId(1, false).commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("three", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
        assertEquals(1, (int) listener.ids.get(1));
    }

    @Test
    @UiThreadTest
    public void pops_to_second_id_inclusive() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one"), 1)
                        .push(TestShard.create("two"), 2)
                        .push(TestShard.create("three"))
                        .commit();
            }
        });

        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.popToId(2, true).commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(2, listener.shards.size());
        assertEquals("three", listener.shards.get(0).getName());
        assertEquals("one", listener.shards.get(1).getName());
        assertEquals(1, (int) listener.ids.get(1));
    }

    @Test
    @UiThreadTest
    public void push_recreate_pop() {
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.setStarting(TestShard.create("one")).push(TestShard.create("two")).commit();
            }
        });
        recreate();
        withPausedMainLooper(new Runnable() {
            @Override
            public void run() {
                backStack.pop().commit();
            }
        });

        assertEquals(0, backStack.size());
        assertEquals(3, listener.shards.size());
        assertEquals("two", listener.shards.get(0).getName());
        assertEquals("two", listener.shards.get(1).getName());
        assertEquals("one", listener.shards.get(2).getName());
    }

    private void recreate() {
        ShardBackStack.State state = backStack.saveState();
        TestShardOwner owner = new TestShardOwner();
        backStack = new ShardBackStack(owner, new FrameLayout(owner.getContext()));
        backStack.restoreState(state);
        backStack.addOnNavigatedListener(listener);
    }

    static class TestOnNavigatedListener implements ShardBackStack.OnNavigatedListener {
        final List<TestShard> shards = new ArrayList<>();
        final List<Integer> ids = new ArrayList<>();

        @Override
        public void onNavigated(Shard shard, @IdRes int id) {
            shards.add((TestShard) shard);
            ids.add(id);
        }
    }
}
