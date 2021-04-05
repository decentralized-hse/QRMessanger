package ru.hattonuri.QRMessanger.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.val;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;

public class HistoryMessageAdapter extends RecyclerView.Adapter<HistoryMessageAdapter.MessageHolder> {
    private final List<Message> messages;

    public HistoryMessageAdapter(List<Message> messages) {
        this.messages = messages;
        for (val i : messages) {
            MessagingUtils.debugError("KEKW", i.getText());
        }
    }

    @NonNull @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessagingUtils.debugError("CREATE", "Create");
        return new MessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messages.get(position);
        DateFormat simple = new SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH);
        Date result = new Date(message.getDate());
        MessagingUtils.debugError("TRY ADD", "Try add %d %s", position, message.getText());
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

        MessageHolder(View view) {
            super(view);
            labelFrom = view.findViewById(R.id.label_from);
            textMessage = view.findViewById(R.id.text_message);
        }
    }
}
