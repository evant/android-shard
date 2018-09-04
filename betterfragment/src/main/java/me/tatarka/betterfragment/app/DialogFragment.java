package me.tatarka.betterfragment.app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public class DialogFragment extends BaseDialogFragment {

    @NonNull
    public Dialog onCreateDialog(@NonNull Context context) {
        return new Dialog(context);
    }

    @Override
    protected final Dialog onCreateDialog(FragmentManager fm, Context context) {
        Dialog dialog = onCreateDialog(context);
        fm.add(this, new DialogContainer(dialog));
        return dialog;
    }

    static class DialogContainer implements Fragment.Container {
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
