package ru.hattonuri.QRMessanger.utils;

import android.util.Base64;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils {
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

    public static <T> void setIfNotNull(T toSet, T value) {
        if (value != null) {
            toSet = value;
        }
    }

    public static long subBits(long ch, int left, int right) {
        return (ch & (1L << right) - 1) >> left;
    }

    //Only for unsigned long
    public static String ltos(long num, int len) {
        StringBuilder ans = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            ans.append((char) subBits(num, i * 16, i * 16 + 16));
        }
        return ans.toString();
    }

    public static String randomBase64(int length) {
        Random random = ThreadLocalRandom.current();
        byte[] ar = new byte[length];
        random.nextBytes(ar);
        return Base64.encodeToString(ar, Base64.DEFAULT);
    }

    public static long stol(String s) {
        long num = 0, bits = 0;
        for (char i : s.toCharArray()) {
            num += i << bits;
            bits += 16;
        }
        return num;
    }
}
