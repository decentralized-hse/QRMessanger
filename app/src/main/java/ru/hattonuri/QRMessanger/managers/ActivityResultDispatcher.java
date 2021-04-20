package ru.hattonuri.QRMessanger.managers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

import lombok.AllArgsConstructor;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.annotations.ActivityReaction;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.groupStructures.Message;
import ru.hattonuri.QRMessanger.utils.CommonUtils;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;
import ru.hattonuri.QRMessanger.utils.DialogUtils;

@AllArgsConstructor
public class ActivityResultDispatcher {
    private final LaunchActivity activity;

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
        activity.getImageManager().updateDecode(bitmap, uri);
        String decoded = activity.getImageManager().getRawText();
        long uuid = CommonUtils.bytesToLong(Base64.decode(decoded.substring(0, 8), Base64.DEFAULT));
        decoded = decoded.substring(8);
        for (Map.Entry<String, ContactsBook.User> userEntry : ContactsBook.getInstance().getUsers().entrySet()) {
            String name = userEntry.getKey();
            long curUuid = userEntry.getValue().getUuid();
            if (curUuid == uuid) {
                Toast.makeText(activity, decoded, Toast.LENGTH_LONG).show();
                HistoryManager.getInstance().addMessage(new Message(System.currentTimeMillis(), name, decoded, true));
                return;
            }
        }
        // if user with uuid doesn't exist
        String finalDecoded = decoded;
        DialogUtils.makeInputDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
            CryptoManager.getInstance().updateEncryptCipher(input, null);
            Toast.makeText(activity, finalDecoded, Toast.LENGTH_LONG).show();
            HistoryManager.getInstance().addMessage(new Message(System.currentTimeMillis(), input, finalDecoded, true));
        }, null);
    }

    @ActivityReaction(requestCodeId = R.integer.add_key_gallery_case)
    private void onAddPublicKeyGallery(Intent intent) {
        Uri uri = intent.getData();
        Bitmap bitmap = ConversionUtils.getUriBitmap(activity, uri, 800);
        activity.getImageManager().update(bitmap, uri);
        DialogUtils.makeInputDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
            CryptoManager.getInstance().updateEncryptCipher(input, ConversionUtils.getPublicKey(activity.getImageManager().getRawText()));
            ContactsBook.getInstance().saveState();
        }, null);
    }
}
