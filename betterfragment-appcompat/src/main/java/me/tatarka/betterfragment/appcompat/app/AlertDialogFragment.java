package me.tatarka.betterfragment.appcompat.app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import me.tatarka.betterfragment.app.BaseDialogFragment;
import me.tatarka.betterfragment.app.FragmentManager;

public class AlertDialogFragment extends BaseDialogFragment {

    @NonNull
    public AlertDialog.Builder onBuildAlertDialog(@NonNull Context context) {
        return new AlertDialog.Builder(context);
    }

    @Override
    protected final Dialog onCreateDialog(FragmentManager fm, Context context) {
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
