package ru.hattonuri.QRMessanger.managers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.RequireInputDialog;
import ru.hattonuri.QRMessanger.annotations.ActivityReaction;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;


public class ActivityResultDispatcher {
    private final LaunchActivity activity;

    public ActivityResultDispatcher(LaunchActivity activity) {
        this.activity = activity;
    }

    public void dispatch(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        for (Method method : this.getClass().getDeclaredMethods()) {
            ActivityReaction annotation = method.getAnnotation(ActivityReaction.class);
            if (annotation != null && activity.getResources().getInteger(annotation.requestCodeId()) == requestCode) {
                try {
                    method.invoke(this, intent);
                } catch (Exception ignored) {

                }
            }
        }
    }

    @ActivityReaction(requestCodeId = R.integer.select_encoded_photo_case)
    private void onSelectEncodedPhoto(Intent intent) {
        Uri uri = intent.getData();
        Bitmap bitmap = ConversionUtils.getUriBitmap(activity, uri, 800);
        activity.getImageManager().updateDecode(bitmap, uri, activity.getCryptoManager());
        Toast.makeText(activity, activity.getImageManager().getRawText(), Toast.LENGTH_LONG).show();
    }

    @ActivityReaction(requestCodeId = R.integer.add_key_gallery_case)
    private void onAddPublicKeyGallery(Intent intent) {
        Uri uri = intent.getData();
        Bitmap bitmap = ConversionUtils.getUriBitmap(activity, uri, 800);
        activity.getImageManager().update(bitmap, uri);
        RequireInputDialog.makeDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
            activity.getCryptoManager().updateEncryptCipher(input, ConversionUtils.getPublicKey(activity.getImageManager().getRawText()));
            activity.getCryptoManager().saveState(activity);
        });
    }

    @ActivityReaction(requestCodeId = R.integer.add_key_camera_case)
    private void onAddPublicKeyCamera(Intent intent) {
        String contents = intent.getStringExtra("SCAN_RESULT");
        Bitmap bitmap = ConversionUtils.encodeQR(contents);
        activity.getImageManager().update(bitmap, null);
        RequireInputDialog.makeDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
            activity.getCryptoManager().updateEncryptCipher(input, ConversionUtils.getPublicKey(activity.getImageManager().getRawText()));
            activity.getCryptoManager().saveState(activity);
        });
    }
}
