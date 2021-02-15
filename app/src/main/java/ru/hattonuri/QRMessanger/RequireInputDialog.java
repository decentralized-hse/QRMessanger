package ru.hattonuri.QRMessanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ru.hattonuri.QRMessanger.interfaces.InputProcess;

public class RequireInputDialog {
    public static void makeDialog(Activity activity, InputProcess runnable) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
        new AlertDialog.Builder(activity).setCancelable(true).setPositiveButton("Ok",
                (inter, which) -> {
                    EditText editText = view.findViewById(R.id.message_edit_text);
                    runnable.run(editText.getText().toString());
                }).setView(view).create().show();
    }
}
