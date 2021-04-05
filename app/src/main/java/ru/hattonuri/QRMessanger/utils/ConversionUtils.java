package ru.hattonuri.QRMessanger.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import ru.hattonuri.QRMessanger.managers.CryptoManager;

public class ConversionUtils {
    public static Uri getUri(Context context, int resid) {
        return (new Uri.Builder())
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getResources().getResourcePackageName(resid))
                .appendPath(context.getResources().getResourceTypeName(resid))
                .appendPath(context.getResources().getResourceEntryName(resid))
                .build();
    }

    public static Bitmap getUriBitmap(Context context, Uri uri, int requiredSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        int width = options.outWidth, height = options.outHeight;
        int scale = (int) Math.ceil(Math.max((double) width / requiredSize, (double) height / requiredSize));

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            return BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getImageUri(Activity inContext, Bitmap inImage) {
        PermissionsUtils.verifyPermissions(inContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap encodeQR(String text) {
        if (text.isEmpty()) {
            return null;
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeQR(Bitmap bitmap) {
        int[] bytes = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(bytes, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), bytes);
        BinaryBitmap binaryMap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        try {
            return reader.decode(binaryMap).getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String parseKey(Key key) {
        if (key == null) {
            return null;
        }
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static PublicKey getPublicKey(String data) {
        if (data == null) {
            return null;
        }
        try {
            return KeyFactory.getInstance(CryptoManager.getAlgorithm()).generatePublic(new X509EncodedKeySpec(Base64.decode(data.getBytes(), Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey getPrivateKey(String data) {
        if (data == null) {
            return null;
        }
        try {
            return KeyFactory.getInstance(CryptoManager.getAlgorithm()).generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(data.getBytes(), Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
