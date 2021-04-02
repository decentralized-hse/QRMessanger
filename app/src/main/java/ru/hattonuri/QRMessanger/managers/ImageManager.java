package ru.hattonuri.QRMessanger.managers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import lombok.Getter;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;

public class ImageManager {
    // rawText - always decoded
    // shownText - if image encoded - text is encoded
    @Getter private String shownText = "";
    @Getter private String rawText = "";
    @Getter private final ImageView imageView;

    @Getter private Uri imageUri;

    public ImageManager(ImageView imageView) {
        this.imageView = imageView;
    }

    public void updateDecode(Bitmap bitmap, @Nullable Uri uri) {
        update(bitmap, uri);
        rawText = CryptoManager.getInstance().decrypt(shownText);
    }

    public void updateEncode(String text) {
        if (text == null) {
            return;
        }
        rawText = text;
        shownText = CryptoManager.getInstance().encrypt(text);
        Bitmap image = ConversionUtils.encodeQR(shownText);
        imageUri = ConversionUtils.getImageUri(getActivity(), image);
        imageView.setImageBitmap(image);
    }

    public void update(String text) {
        if (text == null) {
            return;
        }
        rawText = shownText = text;
        Bitmap bitmap = ConversionUtils.encodeQR(text);
        Uri uri = ConversionUtils.getImageUri(getActivity(), bitmap);
        imageView.setImageBitmap(bitmap);
        imageUri = uri;
    }

    public void update(Bitmap bitmap, @Nullable Uri uri) {
        if (uri == null) {
            uri = ConversionUtils.getImageUri(getActivity(), bitmap);
        }
        imageView.setImageBitmap(bitmap);
        imageUri = uri;
        rawText = ConversionUtils.decodeQR(bitmap);
        shownText = rawText;
    }

    public Activity getActivity() {
        Context context = imageView.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (Activity) context;
    }
}
