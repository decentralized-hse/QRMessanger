package ru.hattonuri.QRMessanger.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsUtils {
    public static void verifyPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {permission};
            ActivityCompat.requestPermissions(activity, permissions, 1001);
        }
    }
}
