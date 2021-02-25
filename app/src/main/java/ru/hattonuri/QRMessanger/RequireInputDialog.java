package ru.hattonuri.QRMessanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ru.hattonuri.QRMessanger.interfaces.InputProcess;

public class RequireInputDialog {
    public static void makeDialog(Activity activity, String name, InputProcess runnable) {
        activity.runOnUiThread(() -> {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
            TextView text = view.findViewById(R.id.dialog_show_text);
            text.setText(name);

            EditText editText = view.findViewById(R.id.dialog_input_text);
            new AlertDialog.Builder(activity).setCancelable(true).setPositiveButton("Ok",
                    (inter, which) -> {
                            runnable.run(editText.getText().toString());
                    }).setView(view).create().show();
        });
    }
}
