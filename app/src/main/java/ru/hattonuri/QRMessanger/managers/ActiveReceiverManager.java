package ru.hattonuri.QRMessanger.managers;

import android.widget.TextView;

import lombok.AllArgsConstructor;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;

@AllArgsConstructor
public class ActiveReceiverManager {
    private final LaunchActivity activity;
    private final TextView activeLabel;

    public void update() {
        String receiver = ContactsBook.getInstance().getActiveReceiverKey();
        if (receiver == null) {
            receiver = "";
        }
        activeLabel.setText(activity.getString(R.string.active_receiver_label).replace("$receiver$", receiver));
    }
}
