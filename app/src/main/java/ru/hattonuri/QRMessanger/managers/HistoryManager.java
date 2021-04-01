package ru.hattonuri.QRMessanger.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.utils.ClassUtils;

public class HistoryManager {
    private final String realmName = "History";
    private final RealmConfiguration config;
    private final HashMap<String, List<Message>> messages = new HashMap<>();

    private static HistoryManager instance = null;

    public HistoryManager() {
        config = new RealmConfiguration.Builder().name(realmName).build();

        for (Message message : Realm.getInstance(config).where(Message.class).findAll().sort("date")) {
            ClassUtils.putIfAbsent(messages, message.getDialer(), new ArrayList<>()).get(message.getDialer()).add(message);
        }
    }

    public void addMessage(Message message) {
        ClassUtils.putIfAbsent(messages, message.getDialer(), new ArrayList<>()).get(message.getDialer()).add(message);
        Realm.getInstance(config).executeTransaction(realm -> realm.insert(message));
    }

    public List<Message> getMessagesFrom(String sender) {
        return ClassUtils.getOrDefault(messages, sender, new ArrayList<>());
    }

    public static HistoryManager getInstance() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new HistoryManager();
                }
            }
        }
        return instance;
    }
}
