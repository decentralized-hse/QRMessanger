package ru.hattonuri.QRMessanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.hattonuri.QRMessanger.interfaces.InputProcess;

public class RequireInputDialog {
    public static void makeDialog(Activity activity,
                                  String name,
                                  InputProcess onAccept,
                                  @Nullable InputProcess onDecline) {
        activity.runOnUiThread(() -> {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
            TextView text = view.findViewById(R.id.dialog_show_text);
            text.setText(name);

            EditText editText = view.findViewById(R.id.dialog_input_text);
            new AlertDialog.Builder(activity).setCancelable(false).
                    setPositiveButton("Accept", (inter, which) -> onAccept.run(editText.getText().toString())).
                    setNegativeButton("Decline", (inter, which) -> {
                        if (onDecline != null) {
                            onDecline.run(editText.getText().toString());
                        }
                    }).setView(view).create().show();
        });
    }
}
