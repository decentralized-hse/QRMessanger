package ru.hattonuri.QRMessanger.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import ru.hattonuri.QRMessanger.BuildConfig;

public class MessagingUtils {
    public static void debug(String tag, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, String.format(message, args));
        }
    }

    public static void debugToast(Context context, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, String.format(message, args), Toast.LENGTH_LONG).show();
        }
    }

    public static void debugError(String tag, String message, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, String.format(message, args));
        }
    }
}
