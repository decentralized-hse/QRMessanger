package ru.hattonuri.QRMessanger;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.managers.CryptoManager;
import ru.hattonuri.QRMessanger.managers.HistoryManager;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView list = findViewById(R.id.messages_list);
        String receiver = CryptoManager.getInstance().getContacts().getActiveReceiverKey();
        List<String> messages = new ArrayList<>();
        for (Message message : HistoryManager.getInstance().getMessagesFrom(receiver)) {
            DateFormat simple = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);
            Date result = new Date(message.getDate());
            if (message.isReceived()) {
                messages.add(String.format("%s(%s) : %s", message.getDialer(), simple.format(result), message.getText()));
            } else {
                messages.add(String.format("You (%s) : %s", simple.format(result), message.getText()));
            }
        }
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages));
    }

    public void onCloseBtnClick(View view) {
        finish();
    }
}