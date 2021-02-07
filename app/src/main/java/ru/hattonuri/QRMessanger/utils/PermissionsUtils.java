package ru.hattonuri.QRMessanger.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

public class PermissionsUtils {
    public static void verifyPermissions(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (activity.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                activity.requestPermissions(new String[]{permission}, 1);
            }
        }
    }
}
