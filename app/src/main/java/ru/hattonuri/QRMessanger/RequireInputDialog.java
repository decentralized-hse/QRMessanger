package ru.hattonuri.QRMessanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

public class RequireInputDialog {
    private EditText editText;
    private String confirmedText;

    public static String makeDialog(Activity activity) {
        RequireInputDialog dialog = new RequireInputDialog();

        View view = activity.getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(true).setPositiveButton("Ok",
                (inter, which) -> {
                    dialog.confirmedText = dialog.editText.getText().toString();
                    dialog.notify();
                });
        builder.setView(view);

        dialog.editText = view.findViewById(R.id.message_edit_text);
        builder.create().show();

        try {
            dialog.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (dialog) {
            return dialog.confirmedText;
        }
    }
}
