package me.tatarka.shard.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A layout inflater wrapper that allows you to compose multiple factories.
 */
public final class CompositeLayoutInflater {

    @Nullable
    private LayoutInflater inflater;
    @Nullable
    private ArrayList<Factory> layoutInflaterFactories;

    public LayoutInflater get(Context context) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
            if (layoutInflaterFactories != null) {
                Factory.Next factory = new BasicFactory(inflater);
                for (final Factory f : layoutInflaterFactories) {
                    factory = applyNext(f, factory);
                    inflater = inflater.cloneInContext(context);
                    inflater.setFactory2(toFactory2(factory));
                }
            }
        }
        return inflater;
    }

    public void addFactory(@NonNull Factory factory) {
        inflater = null;
        if (layoutInflaterFactories == null) {
            layoutInflaterFactories = new ArrayList<>();
        }
        layoutInflaterFactories.add(factory);
    }

    public void removeFactory(@NonNull Factory factory) {
        inflater = null;
        if (layoutInflaterFactories != null) {
            layoutInflaterFactories.remove(factory);
        }
    }

    private static Factory.Next applyNext(final Factory factory, final Factory.Next next) {
        return new Factory.Next() {
            @Override
            public View createView(View parent, String name, Context context, AttributeSet attrs) {
                return factory.onCreateView(next, parent, name, context, attrs);
            }
        };
    }

    private static LayoutInflater.Factory2 toFactory2(final Factory.Next factory) {
        return new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                return factory.createView(parent, name, context, attrs);
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return null;
            }
        };
    }

    public interface Factory {
        @Nullable
        View onCreateView(Factory.Next next, View parent, String name, Context context, AttributeSet attrs);

        interface Next {
            @Nullable
            View createView(View parent, String name, Context context, AttributeSet attrs);
        }
    }

    private static class BasicFactory implements Factory.Next {
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        private final LayoutInflater inflater;

        private BasicFactory(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Nullable
        @Override
        public View createView(View parent, String name, Context context, AttributeSet attrs) {
            for (String prefix : sClassPrefixList) {
                try {
                    View view = inflater.createView(name, prefix, attrs);
                    if (view != null) {
                        return view;
                    }
                } catch (ClassNotFoundException e) {
                    // In this case we want to let the base class take a crack
                    // at it.
                }
            }
            return null;
        }
    }
}
