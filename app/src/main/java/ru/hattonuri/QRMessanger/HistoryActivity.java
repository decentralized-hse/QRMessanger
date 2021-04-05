package ru.hattonuri.QRMessanger;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.hattonuri.QRMessanger.adapters.HistoryMessageAdapter;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.managers.HistoryManager;

//TODO ADD MESSAGES DELETION
//TODO CHECK FOR NULL RECEIVER
public class HistoryActivity extends AppCompatActivity {
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        list = findViewById(R.id.messages_list);
        String receiver = ContactsBook.getInstance().getActiveReceiverKey();
        HistoryMessageAdapter adapter = new HistoryMessageAdapter(this, HistoryManager.getInstance().getMessagesFrom(receiver));
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    public void onCloseBtnClick(View view) {
        finish();
    }
}