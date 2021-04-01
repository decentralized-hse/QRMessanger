package ru.hattonuri.QRMessanger.utils;

import java.util.Map;

public class ClassUtils {
    public static <K, V> Map<K, V> putIfAbsent(Map<K, V> map, K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
        return map;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V value) {
        V val = map.get(key);
        return (val == null ? value : val);
    }
}
