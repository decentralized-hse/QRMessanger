package ru.hattonuri.QRMessanger.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.interfaces.InputProcess;
import ru.hattonuri.QRMessanger.managers.CryptoManager;

public class DialogUtils {
    public static void makeInputDialog(Activity activity,
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

    public static void makeAddContactDialog(LaunchActivity activity, String text) {
        activity.runOnUiThread(() -> {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_input_dialog, null);
            TextView zxc = view.findViewById(R.id.dialog_show_text);
            zxc.setText(activity.getResources().getString(R.string.dialog_input_name));

            EditText editText = view.findViewById(R.id.dialog_input_text);
            AlertDialog dialog = new AlertDialog.Builder(activity).
                    setPositiveButton("Accept", null).
                    setNegativeButton("Decline", null).setView(view).show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                        String input = editText.getText().toString();
                        ContactsBook.User collision = ContactsBook.getInstance().getUsers().get(input);
                        if (input.isEmpty()) {
                            Toast.makeText(activity, activity.getString(R.string.msg_name_empty), Toast.LENGTH_SHORT).show();
                        } else if (collision != null && collision.getKey() != null) {
                            Toast.makeText(activity, activity.getString(R.string.msg_user_exists), Toast.LENGTH_LONG).show();
                        } else {
                            CryptoManager.getInstance().addContact(input, ConversionUtils.getPublicKey(text));
                            if (input.equals(ContactsBook.getInstance().getActiveReceiverKey())) {
                                CryptoManager.getInstance().updateEncryptCipher(input);
                            }
                            dialog.dismiss();
                        }
                    }
            );
            activity.getImageManager().update(text);
        });
    }

    public static void makeDialog(Activity activity,
                                  String name,
                                  Runnable onAccept,
                                  Runnable onDecline) {
        activity.runOnUiThread(() -> new AlertDialog.Builder(activity).setCancelable(false).
                setPositiveButton("Accept", (inter, which) -> onAccept.run()).
                setNegativeButton("Decline", (inter, which) -> {
                    if (onDecline != null) {
                        onDecline.run();
                    }
                }).setMessage(name).create().show());
    }
}
