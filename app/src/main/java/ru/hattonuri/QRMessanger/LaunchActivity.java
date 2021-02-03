package ru.hattonuri.QRMessanger;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ru.hattonuri.QRMessanger.utils.ConversionUtils;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;
import ru.hattonuri.QRMessanger.utils.PermissionsUtils;

public class LaunchActivity extends AppCompatActivity {
    private EditText editText;
    private ImageView imageView;

    private Uri imageUri = null;

    private void setContents() {
        editText = findViewById(R.id.message_edit_text);
        imageView = findViewById(R.id.imageView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContents();
    }

    public void onEncodeBtnClick(View view) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        Bitmap qr = ConversionUtils.encodeQR(text);
        if (qr != null) {
            imageView.setImageBitmap(qr);
            PermissionsUtils.verifyPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            imageUri = ConversionUtils.getImageUri(this, qr);
        }
    }

    public void onDecodeBtnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, R.integer.select_encoded_photo);
    }

    public void onSendBtnClick(View view) {
        if (imageUri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(intent, "Send to"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case R.integer.select_encoded_photo:
                if (resultCode == RESULT_OK) {
                    if (intent == null) {
                        MessagingUtils.debugError("ACT_RESULT", "Null intent %d %d", requestCode, resultCode);
                        return;
                    }
                    Uri uri = intent.getData();// Get intent
                    Bitmap bitmap = ConversionUtils.getUriBitmap(this, uri, 800);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        imageUri = uri;
                        Toast.makeText(this, ConversionUtils.decodeQR(bitmap), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                MessagingUtils.debugError("ACT_RESULT", "Wrong requestCode %d %d", requestCode, resultCode);
        }
    }
}