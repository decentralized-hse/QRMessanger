package ru.hattonuri.QRMessanger.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import lombok.Getter;
import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.utils.ClassUtils;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;

public class HistoryManager {
    private final String realmName = "History";
    private final RealmConfiguration config;
    private final HashMap<String, List<Message>> messages = new HashMap<>();

    @Getter
    private static final HistoryManager instance = new HistoryManager();

    public HistoryManager() {
        config = new RealmConfiguration.Builder().name(realmName).build();
        for (Message message : Realm.getInstance(config).where(Message.class).findAll().sort("date")) {
            ClassUtils.putIfAbsent(messages, message.getDialer(), new ArrayList<>()).get(message.getDialer()).add(message);
            MessagingUtils.debugError("Msg", "%s %s", message.getDialer(), message.getText());
        }
    }

    public void addMessage(Message message) {
        ClassUtils.putIfAbsent(messages, message.getDialer(), new ArrayList<>()).get(message.getDialer()).add(message);
        Realm.getInstance(config).executeTransactionAsync(realm -> realm.insert(message));
    }

    public void removeMessage(int idx) {
        String receiver = CryptoManager.getInstance().getContacts().getActiveReceiverKey();
        if (receiver == null || !messages.containsKey(receiver)) {
            return;
        }
        List<Message> list = messages.get(receiver);
        Message toDelete = list.get(idx);
        list.remove(idx);
        Realm.getInstance(config).executeTransaction(
                realm -> realm.where(Message.class).equalTo("date", toDelete.getDate()).findFirst().deleteFromRealm());
    }

    public List<Message> getMessagesFrom(String sender) {
        return ClassUtils.getOrDefault(messages, sender, new ArrayList<>());
    }

    public void removeMessages(String sender) {
        messages.remove(sender);
        Realm.getInstance(config).executeTransaction(
                realm -> realm.where(Message.class).equalTo("dialer", sender).findAll().deleteAllFromRealm()
        );
    }
}
