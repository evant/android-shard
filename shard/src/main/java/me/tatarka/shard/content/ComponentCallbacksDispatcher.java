package me.tatarka.shard.content;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import me.tatarka.shard.app.ShardActivity;
import me.tatarka.shard.app.ShardOwner;

/**
 * Helps with implementing {@link ComponentCallbacks}. Will dispatch component callbacks to any
 * registered listeners.
 *
 * @see ShardActivity for an example of how to use.
 */
public class ComponentCallbacksDispatcher implements ComponentCallbacks {

    private final ArrayList<android.content.ComponentCallbacks2> componentCallbacks = new ArrayList<>();
    final Context context;
    final Lifecycle lifecycle;

    public ComponentCallbacksDispatcher(ShardOwner owner) {
        this.context = owner.getContext();
        lifecycle = owner.getLifecycle();
        Callbacks callbacks = new Callbacks();
        context.registerComponentCallbacks(callbacks);
        owner.getLifecycle().addObserver(callbacks);
    }

    @Override
    public void addOnConfigurationChangedListener(@NonNull OnConfigurationChangedListener listener) {
        componentCallbacks.add(new ConfigurationChangedComponentCallbacksAdapter(listener));
    }

    @Override
    public void removeOnConfigurationChangedListener(@NonNull OnConfigurationChangedListener listener) {
        for (int i = 0, size = componentCallbacks.size(); i < size; i++) {
            android.content.ComponentCallbacks callback = componentCallbacks.get(i);
            if (callback instanceof ConfigurationChangedComponentCallbacksAdapter && ((ConfigurationChangedComponentCallbacksAdapter) callback).listener.equals(listener)) {
                componentCallbacks.remove(i);
                break;
            }
        }
    }

    @Override
    public void addOnTrimMemoryListener(@NonNull OnTrimMemoryListener listener) {
        componentCallbacks.add(new TrimMemoryComponentCallbacksAdapter(listener));
    }

    @Override
    public void removeOnTrimMemoryListener(@NonNull OnTrimMemoryListener listener) {
        for (int i = 0, size = componentCallbacks.size(); i < size; i++) {
            android.content.ComponentCallbacks callback = componentCallbacks.get(i);
            if (callback instanceof TrimMemoryComponentCallbacksAdapter && ((TrimMemoryComponentCallbacksAdapter) callback).listener.equals(listener)) {
                componentCallbacks.remove(callback);
                break;
            }
        }
    }

    class Callbacks implements ComponentCallbacks2, LifecycleEventObserver {

        @Override
        public void onTrimMemory(int level) {
            for (int i = 0, size = componentCallbacks.size(); i < size; i++) {
                android.content.ComponentCallbacks2 listener = componentCallbacks.get(i);
                listener.onTrimMemory(level);
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            for (int i = 0, size = componentCallbacks.size(); i < size; i++) {
                ComponentCallbacks2 listener = componentCallbacks.get(i);
                listener.onConfigurationChanged(newConfig);
            }
        }

        @Override
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                lifecycle.removeObserver(this);
                context.unregisterComponentCallbacks(this);
            }
        }

        @Override
        public void onLowMemory() {

        }
    }

    static class ConfigurationChangedComponentCallbacksAdapter implements android.content.ComponentCallbacks2 {

        final OnConfigurationChangedListener listener;

        ConfigurationChangedComponentCallbacksAdapter(OnConfigurationChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            listener.onConfigurationChanged(newConfig);
        }

        @Override
        public void onLowMemory() {

        }

        @Override
        public void onTrimMemory(int level) {

        }
    }

    static class TrimMemoryComponentCallbacksAdapter implements ComponentCallbacks2 {

        final OnTrimMemoryListener listener;

        TrimMemoryComponentCallbacksAdapter(OnTrimMemoryListener listener) {
            this.listener = listener;
        }

        @Override
        public void onTrimMemory(int level) {
            listener.onTrimMemory(level);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {

        }
    }
}
