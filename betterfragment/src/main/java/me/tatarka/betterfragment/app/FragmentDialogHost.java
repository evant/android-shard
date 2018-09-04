package me.tatarka.betterfragment.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import me.tatarka.betterfragment.state.StateSaver;

public class FragmentDialogHost {

    private static final String DIALOG_STATE = "me.tatarka.betterfragment.widget.FragmentDialogHost";
    private static final String STATES = "states";

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
        owner.getStateStore().addStateSaver(DIALOG_STATE, callbacks);
        owner.getLifecycle().addObserver(callbacks);
    }

    @NonNull
    public Fragment.Factory getFragmentFactory() {
        return factory;
    }

    public void show(Class<? extends Fragment> fragmentClass) {
        BaseDialogFragment fragment = (BaseDialogFragment) factory.newInstance(fragmentClass);
        dialogFragments.add(fragment);
        doShow(fragment);
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

    class DialogHostCallbacks implements StateSaver, LifecycleObserver {

        @Override
        public void onRestoreState(@NonNull Bundle instanceState) {
            List<Fragment.State> states = instanceState.getParcelableArrayList(STATES);
            for (Fragment.State fragmentState : states) {
                BaseDialogFragment fragment = (BaseDialogFragment) factory.newInstance(fragmentState.getFragmentClass());
                fm.restoreState(fragment, fragmentState);
                dialogFragments.add(fragment);
                doShow(fragment);
            }
        }

        @Override
        public void onSaveState(@NonNull Bundle outState) {
            ArrayList<Fragment.State> states = new ArrayList<>();
            for (BaseDialogFragment fragment : dialogFragments) {
                states.add(fm.saveState(fragment));
            }
            outState.putParcelableArrayList(STATES, states);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            for (BaseDialogFragment fragment : dialogFragments) {
                fragment.destroyDialog();
            }
        }
    }
}
