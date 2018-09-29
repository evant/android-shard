package me.tatarka.betterfragment.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import me.tatarka.betterfragment.state.InstanceStateSaver;

public class FragmentDialogHost {

    private static final String DIALOG_STATE = "me.tatarka.betterfragment.widget.FragmentDialogHost";

    private final FragmentOwner owner;
    private final FragmentManager fm;
    private final ArrayList<BaseDialogFragment> dialogFragments = new ArrayList<>();
    private final Fragment.Factory factory;

    public FragmentDialogHost(Context context) {
        this(context, Fragment.DefaultFactory.getInstance());
    }

    public FragmentDialogHost(Context context, Fragment.Factory fragmentFactory) {
        this(FragmentOwners.get(context), fragmentFactory);
    }

    public FragmentDialogHost(FragmentOwner owner) {
        this(owner, Fragment.DefaultFactory.getInstance());
    }

    public FragmentDialogHost(FragmentOwner owner, Fragment.Factory fragmentFactory) {
        this.owner = owner;
        this.factory = fragmentFactory;
        fm = new FragmentManager(owner);
        DialogHostCallbacks callbacks = new DialogHostCallbacks();
        owner.getInstanceStateStore().add(DIALOG_STATE, callbacks);
        owner.getLifecycle().addObserver(callbacks);
    }

    @NonNull
    public Fragment.Factory getFragmentFactory() {
        return factory;
    }

    public void show(BaseDialogFragment fragment) {
        dialogFragments.add(fragment);
        doShow(fragment);
    }

    void doShow(final BaseDialogFragment fragment) {
        if (fragment.isShowing()) {
            return;
        }
        Dialog dialog = fragment.createDialog(fm, owner.getContext());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                fragment.onDismiss(dialog);
                dialogFragments.remove(fragment);
                fm.remove(fragment);
            }
        });
        dialog.setOnCancelListener(fragment);
        dialog.show();
    }

    class DialogHostCallbacks implements InstanceStateSaver<State>, LifecycleObserver {

        @Nullable
        @Override
        public State onSaveInstanceState() {
            int size = dialogFragments.size();
            if (size == 0) {
                return null;
            }
            ArrayMap<String, Fragment.State> states = new ArrayMap<>(size);
            for (int i = 0; i < size; i++) {
                Fragment fragment = dialogFragments.get(i);
                states.put(fragment.getClass().getName(), fm.saveState(fragment));
            }
            return new State(states);
        }

        @Override
        public void onRestoreInstanceState(@NonNull State instanceState) {
            ArrayMap<String, Fragment.State> states = instanceState.fragmentStates;
            for (int i = 0, size = states.size(); i < size; i++) {
                String name = states.keyAt(i);
                Fragment.State fragmentState = states.valueAt(i);
                BaseDialogFragment fragment = factory.newInstance(name, fragmentState.getArgs());
                fm.restoreState(fragment, fragmentState);
                dialogFragments.add(fragment);
                doShow(fragment);
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            for (BaseDialogFragment fragment : dialogFragments) {
                fragment.destroyDialog();
            }
        }
    }

    public static class State implements Parcelable {
        final ArrayMap<String, Fragment.State> fragmentStates;

        State(ArrayMap<String, Fragment.State> fragmentStates) {
            this.fragmentStates = fragmentStates;
        }

        State(Parcel in) {
            fragmentStates = new ArrayMap<>();
            in.readMap(fragmentStates, getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeMap(fragmentStates);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
}
