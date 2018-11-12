package me.tatarka.shard.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * A {@code Shard} that can be shown in an {@link AlertDialog}. The dialog will automatically be
 * retained across configuration changes until it's dismissed.
 */
public class AlertDialogShard extends BaseDialogShard {

    /**
     * Creates a new {@link AlertDialog} to be shown. The default implementation simply calls
     * {@code new AlertDialog.Builder(context)}. Do not add cancel/dismiss listeners here as they
     * will be overwritten by the {@link ShardDialogHost} implementation. Override
     * {@link #onCancel(DialogInterface)} and {@link #onDismiss(DialogInterface)} instead. You
     * should prefer calling {@link #setContentView(View)} in {@link #onCreate()} instead of setting
     * the content view here as it makes easier to reuse the same view when the shard is showing in
     * a dialog or not.
     */
    @NonNull
    public AlertDialog.Builder onBuildAlertDialog(@NonNull Context context) {
        return new AlertDialog.Builder(context);
    }

    @Override
    protected final Dialog onCreateDialog(ShardManager fm, Context context) {
        AlertDialog.Builder builder = onBuildAlertDialog(context);
        AlertDialogContainer container = new AlertDialogContainer(builder);
        fm.add(this, container);
        return container.createDialog();
    }

    static class AlertDialogContainer implements Container {
        private AlertDialog.Builder dialogBuilder;

        AlertDialogContainer(AlertDialog.Builder dialogBuilder) {
            this.dialogBuilder = dialogBuilder;
        }

        @Override
        public void addView(View view) {
            if (dialogBuilder == null) {
                throw new IllegalStateException("Cannot modify view from created dialog");
            }
            dialogBuilder.setView(view);
        }

        @Override
        public void removeView(View view) {
            if (dialogBuilder != null) {
                dialogBuilder.setView(null);
            }
        }

        private Dialog createDialog() {
            Dialog dialog = dialogBuilder.create();
            dialogBuilder = null;
            return dialog;
        }
    }
}
