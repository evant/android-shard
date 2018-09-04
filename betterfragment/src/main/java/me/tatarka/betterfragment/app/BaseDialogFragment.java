package me.tatarka.betterfragment.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseDialogFragment extends Fragment implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    @Nullable
    private Dialog dialog;

    Dialog createDialog(FragmentManager fm, Context context) {
        dialog = onCreateDialog(fm, context);
        return dialog;
    }

    void destroyDialog() {
        if (dialog != null) {
            dialog.setOnDismissListener(null);
            dialog.setOnCancelListener(null);
            dialog.dismiss();
        }
    }

    protected abstract Dialog onCreateDialog(FragmentManager fm, Context context);

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }
}

