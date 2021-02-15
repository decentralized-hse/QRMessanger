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
    private final ImageView imageView;

    @Getter private Uri imageUri;

    public ImageManager(ImageView imageView) {
        this.imageView = imageView;
    }

    public void updateDecode(Bitmap bitmap, @Nullable Uri uri, CryptoManager cryptoManager) {
        update(bitmap, uri);
        rawText = cryptoManager.decrypt(shownText);
    }

    public void updateEncode(String text, CryptoManager cryptoManager) {
        if (text == null) {
            return;
        }
        rawText = text;
        shownText = cryptoManager.encrypt(text);
        Bitmap image = ConversionUtils.encodeQR(shownText);
        imageUri = ConversionUtils.getImageUri(getActivity(), image);
        imageView.setImageBitmap(image);
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
