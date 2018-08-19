package me.tatarka.betterfragment.test;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class TestLifecycleObserver implements LifecycleObserver {

    private final List<Lifecycle.Event> events = new ArrayList<>();
    private Lifecycle.State currentState = Lifecycle.State.INITIALIZED;

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onLifecycleEvent(LifecycleOwner owner, Lifecycle.Event event) {
        currentState = owner.getLifecycle().getCurrentState();
        events.add(event);
    }

    public Lifecycle.State getCurrentState() {
        return currentState;
    }

    public Lifecycle.Event[] getEvents() {
        return events.toArray(new Lifecycle.Event[0]);
    }
}
