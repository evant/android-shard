package me.tatarka.shard.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class BaseDialogShard extends Shard implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    @Nullable
    private Dialog dialog;

    Dialog createDialog(ShardManager fm, Context context) {
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

    protected abstract Dialog onCreateDialog(ShardManager fm, Context context);

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

