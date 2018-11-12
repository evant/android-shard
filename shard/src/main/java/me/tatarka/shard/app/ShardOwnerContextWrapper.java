package me.tatarka.shard.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * Allows obtaining a {@link Shard} from {@link #getSystemService(String)}.
 *
 * @see ShardOwners
 */
public class ShardOwnerContextWrapper extends ContextWrapper implements LifecycleObserver {
    static final String SHARD_OWNER = "me.tatarka.shard.app.ShardOwner";
    private final Object owner;
    private LayoutInflater inflater;

    public <Owner extends ViewModelStoreOwner & LifecycleOwner> ShardOwnerContextWrapper(Context base, Owner owner) {
        super(base);
        this.owner = owner;
    }

    @Override
    public Object getSystemService(String name) {
        if (SHARD_OWNER.equals(name)) {
            return owner;
        }
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (inflater == null) {
                inflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return inflater;
        }
        return getBaseContext().getSystemService(name);
    }
}
