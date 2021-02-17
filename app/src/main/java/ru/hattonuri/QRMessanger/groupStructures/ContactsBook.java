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
import ru.hattonuri.QRMessanger.utils.ConversionUtils;

public class ContactsBook {
    @Getter @Setter
    private PrivateKey privateKey;
    @Getter @Setter
    private PublicKey activeReceiverKey;

    @Getter @Setter
    private Map<String, PublicKey> users = new HashMap<>();

    public static class ContactsSerializer implements JsonDeserializer<ContactsBook>, JsonSerializer<ContactsBook> {
        @Override
        public ContactsBook deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ContactsBook result = new ContactsBook();
            JsonObject jsonObject = json.getAsJsonObject();
            result.privateKey = ConversionUtils.getPrivateKey(jsonObject.get("privateKey").getAsString());
            result.activeReceiverKey = ConversionUtils.getPublicKey(jsonObject.get("active").getAsString());

            result.users = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject("users").entrySet()) {
                result.users.put(entry.getKey(), ConversionUtils.getPublicKey(entry.getValue().getAsString()));
            }
            return result;
        }

        @Override
        public JsonElement serialize(ContactsBook src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("privateKey", ConversionUtils.parseKey(src.privateKey));
            result.addProperty("active", ConversionUtils.parseKey(src.activeReceiverKey));

            JsonObject usersMap = new JsonObject();
            for (Map.Entry<String, PublicKey> entry : src.users.entrySet()) {
                usersMap.addProperty(entry.getKey(), ConversionUtils.parseKey(entry.getValue()));
            }
            result.add("users", usersMap);

            return result;
        }
    }
}
