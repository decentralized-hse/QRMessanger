package ru.hattonuri.QRMessanger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import ru.hattonuri.QRMessanger.HistoryActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.managers.HistoryManager;

public class HistoryMessageAdapter extends RecyclerView.Adapter<HistoryMessageAdapter.MessageHolder> {
    private final List<Message> messages;
    private final HistoryActivity activity;

    public HistoryMessageAdapter(HistoryActivity activity, List<Message> messages) {
        this.messages = messages;
        this.activity = activity;
    }

    @NonNull @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messages.get(position);
        DateFormat simple = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
        Date result = new Date(message.getDate());

        holder.getDeleteMsgBtn().setOnClickListener(v -> {
            HistoryManager.getInstance().removeMessage(position);
            notifyDataSetChanged();
        });
        if (message.isReceived()) {
            holder.getLabelFrom().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.getTextMessage().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.getLabelFrom().setText(String.format("%s(%s):", message.getDialer(), simple.format(result)));
        } else {
            holder.getLabelFrom().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.getTextMessage().setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.getLabelFrom().setText(String.format("%s", simple.format(result)));
        }
        holder.getTextMessage().setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        @Getter
        private final TextView labelFrom, textMessage;
        @Getter
        private final Button deleteMsgBtn;

        MessageHolder(View view) {
            super(view);
            labelFrom = view.findViewById(R.id.label_from);
            textMessage = view.findViewById(R.id.text_message);
            deleteMsgBtn = view.findViewById(R.id.delete_msg_btn);
        }
    }
}
