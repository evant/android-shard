package me.tatarka.betterfragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;

class FragmentContextWrapper extends ContextWrapper {
    static final String FRAGMENT_OWNER = "me.tatarka.betterfragment.FragmentOwner";
    private final Object owner;
    private LayoutInflater inflater;

    public <Owner extends ViewModelStoreOwner & LifecycleOwner> FragmentContextWrapper(Context base, Owner owner) {
        super(base);
        this.owner = owner;
    }

    @Override
    public Object getSystemService(String name) {
        if (FRAGMENT_OWNER.equals(name)) {
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
