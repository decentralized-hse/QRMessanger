package ru.hattonuri.QRMessanger;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.hattonuri.QRMessanger.adapters.HistoryMessageAdapter;
import ru.hattonuri.QRMessanger.managers.CryptoManager;
import ru.hattonuri.QRMessanger.managers.HistoryManager;

//TODO ADD MESSAGES DELETION
public class HistoryActivity extends AppCompatActivity {
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        list = findViewById(R.id.messages_list);
        String receiver = CryptoManager.getInstance().getContacts().getActiveReceiverKey();
        HistoryMessageAdapter adapter = new HistoryMessageAdapter(HistoryManager.getInstance().getMessagesFrom(receiver));
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    public void onCloseBtnClick(View view) {
        finish();
    }
}