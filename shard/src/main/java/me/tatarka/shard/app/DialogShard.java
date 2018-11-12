package me.tatarka.shard.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * A {@code Shard} that can be shown in a {@link Dialog}. The dialog will automatically be retained
 * across configuration changes until it's dismissed.
 */
public class DialogShard extends BaseDialogShard {

    /**
     * Creates a new {@link Dialog} to be shown. The default implementation simply calls
     * {@code new Dialog(context)}. Do not add cancel/dismiss listeners here as they will be
     * overwritten by the {@link ShardDialogHost} implementation. Override
     * {@link #onCancel(DialogInterface)} and {@link #onDismiss(DialogInterface)} instead. You
     * should prefer calling {@link #setContentView(View)} in {@link #onCreate()} instead of setting
     * the content view here as it makes easier to reuse the same view when the shard is showing in
     * a dialog or not.
     */
    @NonNull
    public Dialog onCreateDialog(@NonNull Context context) {
        return new Dialog(context);
    }

    @Override
    protected final Dialog onCreateDialog(ShardManager fm, Context context) {
        Dialog dialog = onCreateDialog(context);
        fm.add(this, new DialogContainer(dialog));
        return dialog;
    }

    static class DialogContainer implements Shard.Container {
        final Dialog dialog;

        DialogContainer(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void addView(View view) {
            dialog.setContentView(view);
        }

        @Override
        public void removeView(View view) {
            // Not supported
        }
    }

}
