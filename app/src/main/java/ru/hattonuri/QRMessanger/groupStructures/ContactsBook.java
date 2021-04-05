package ru.hattonuri.QRMessanger.groupStructures;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;
import ru.hattonuri.QRMessanger.utils.SaveUtils;

public class ContactsBook {
    @Getter @Setter
    private PrivateKey privateKey;
    @Getter @Setter
    private PublicKey receivingKey;
    @Getter @Setter
    private String activeReceiverKey;
    @Getter @Setter
    private Map<String, PublicKey> users = new HashMap<>();

    private static ContactsBook instance;
    public static ContactsBook getInstance() {
        if (instance == null) {
            instance = SaveUtils.load(LaunchActivity.getInstance(), ContactsBook.class, null, "contacts.json");
        }
        return instance;
    }

    public void saveState() {
        SaveUtils.save(LaunchActivity.getInstance(), this, null, "contacts.json");
    }

    public PublicKey getDialer() {
        return activeReceiverKey == null ? null : users.get(activeReceiverKey);
    }

    public static class ContactsSerializer implements JsonDeserializer<ContactsBook>, JsonSerializer<ContactsBook> {
        @Override
        public ContactsBook deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ContactsBook result = new ContactsBook();
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement buffer = jsonObject.get("privateKey");
            if (buffer != null) {
                result.privateKey = ConversionUtils.getPrivateKey(buffer.getAsString());
            }
            buffer = jsonObject.get("receivingKey");
            if (buffer != null) {
                result.receivingKey = ConversionUtils.getPublicKey(buffer.getAsString());
            }
            buffer = jsonObject.get("active");
            if (buffer != null) {
                result.activeReceiverKey = jsonObject.get("active").getAsString();
            }

            result.users = new HashMap<>();
            JsonObject users = jsonObject.getAsJsonObject("users");
            if (users != null) {
                for (Map.Entry<String, JsonElement> entry : users.entrySet()) {
                    result.users.put(entry.getKey(), ConversionUtils.getPublicKey(entry.getValue().getAsString()));
                }
            }

            return result;
        }

        @Override
        public JsonElement serialize(ContactsBook src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("privateKey", ConversionUtils.parseKey(src.privateKey));
            result.addProperty("receivingKey", ConversionUtils.parseKey(src.receivingKey));
            result.addProperty("active", src.activeReceiverKey);

            JsonObject usersMap = new JsonObject();
            for (Map.Entry<String, PublicKey> entry : src.users.entrySet()) {
                usersMap.addProperty(entry.getKey(), ConversionUtils.parseKey(entry.getValue()));
            }
            result.add("users", usersMap);
            return result;
        }
    }
}
