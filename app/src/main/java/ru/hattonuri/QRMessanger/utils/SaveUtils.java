package ru.hattonuri.QRMessanger.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SaveUtils {
    private static final String defaultPath = "saves";

    public static <T> void saveToBundle(Bundle bundle, T object, String name) {
        if (bundle == null) {
            return;
        }
        bundle.putString(name, toJsonString(object));
    }

    public static <T> T loadFromBundle(Bundle bundle, String name, Class<T> clazz) {
        if (bundle == null) {
            return null;
        }
        return fromJsonString(bundle.getString(name), clazz);
    }

    public static <T> void save(Context context, T object, @Nullable String root, String fileName) {
        if (!isStorageAvailable() || isStorageReadOnly()) {
            return;
        }
        if (root == null) {
            root = defaultPath;
        }
        File saveFile = new File(context.getExternalFilesDir(root), fileName);
        try {
            FileOutputStream output = new FileOutputStream(saveFile);
            output.write(toJsonString(object).getBytes());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T load(Context context, Class<T> clazz, @Nullable String root, String fileName) {
        if (!isStorageAvailable()) {
            return null;
        }
        if (root == null) {
            root = defaultPath;
        }
        File saveFile = new File(context.getExternalFilesDir(root), fileName);
        if (!saveFile.exists()) {
            return null;
        }
        try {
            FileInputStream input = new FileInputStream(saveFile);
            byte[] bytes = new byte[(int) saveFile.length()];
            if (input.read(bytes) != bytes.length) {
                throw new Exception("Can't read that length");
            }
            return fromJsonString(new String(bytes), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String toJsonString(T object) {
        try {
            Class<? extends JsonSerializer<?>> serializer = getSerializer(object.getClass());
            Gson gson;
            if (serializer != null) {
                gson = new GsonBuilder().registerTypeAdapter(object.getClass(), serializer.newInstance()).create();
            } else {
                gson = new GsonBuilder().create();
            }
            return gson.toJson(object, object.getClass());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            Class<? extends JsonDeserializer<T>> deserializer = getDeserializer(clazz);
            Gson gson;
            if (deserializer != null) {
                gson = new GsonBuilder().registerTypeAdapter(clazz, deserializer.newInstance()).create();
            } else {
                gson = new GsonBuilder().create();
            }
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isStorageReadOnly() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public static <T> Class<? extends JsonSerializer<T>> getSerializer(Class<T> clazz) {
        for (Class<?> member : clazz.getDeclaredClasses()) {
            if (JsonSerializer.class.isAssignableFrom(member)) {
                return (Class<? extends JsonSerializer<T>>) member;
            }
        }
        return null;
    }

    public static <T> Class<? extends JsonDeserializer<T>> getDeserializer(Class<T> clazz) {
        for (Class<?> member : clazz.getDeclaredClasses()) {
            if (JsonDeserializer.class.isAssignableFrom(member)) {
                return (Class<? extends JsonDeserializer<T>>) member;
            }
        }
        return null;
    }
}
