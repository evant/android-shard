package me.tatarka.shard.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * The base class for shards that can be shown in dialogs.
 *
 * @see DialogShard
 * @see AlertDialogShard
 */
public abstract class BaseDialogShard extends Shard implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    @Nullable
    private Dialog dialog;
    private boolean isInDialog;

    Dialog createDialog(ShardManager fm, Context context) {
        isInDialog = true;
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

    /**
     * Dismiss the dialog if it is showing.
     *
     * @see Dialog#dismiss()
     */
    public final void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Returns true if the shard is or will be shown in a {@link Dialog}.
     */
    public final boolean isInDialog() {
        return isInDialog;
    }

    /**
     * Returns true if the {@link Dialog} is showing.
     *
     * @see Dialog#isShowing()
     */
    public final boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * Called when the {@link Dialog} is canceled.
     *
     * @see android.content.DialogInterface.OnCancelListener#onCancel(DialogInterface)
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {

    }

    /**
     * Called when the {@link Dialog} is dismissed.
     *
     * @see DialogInterface.OnDismissListener#onDismiss(DialogInterface)
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

    }
}

