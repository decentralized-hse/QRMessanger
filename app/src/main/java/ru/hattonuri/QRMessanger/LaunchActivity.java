package ru.hattonuri.QRMessanger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.PublicKey;

import ru.hattonuri.QRMessanger.managers.CryptoManager;
import ru.hattonuri.QRMessanger.managers.ImageManager;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;
import ru.hattonuri.QRMessanger.utils.PermissionsUtils;

public class LaunchActivity extends AppCompatActivity {
    private EditText editText;
    private ImageManager imageManager;
    private CryptoManager cryptoManager;

    private void setContents() {
        editText = findViewById(R.id.message_edit_text);
        imageManager = new ImageManager(findViewById(R.id.imageView));
        cryptoManager = new CryptoManager();
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
        if (text.length() > getResources().getInteger(R.integer.max_msg_len)) {
            String answer = String.format(getString(R.string.msg_len_overflow), getResources().getInteger(R.integer.max_msg_len));
            Toast.makeText(this, answer, Toast.LENGTH_LONG).show();
            return;
        }
        imageManager.updateEncode(text, cryptoManager);
    }

    public void onDecodeBtnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, getResources().getInteger(R.integer.select_encoded_photo_case));
    }

    public void onSendBtnClick(View view) {
        if (imageManager.getImageUri() == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageManager.getImageUri());
        startActivity(Intent.createChooser(intent, "Send to"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }


    // TODO Make here a dispatcher
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK || intent == null) {
            return;
        }
        Uri uri = intent.getData();
        Bitmap bitmap = ConversionUtils.getUriBitmap(this, uri, 800);
        if (requestCode == getResources().getInteger(R.integer.select_encoded_photo_case)) {
            imageManager.updateDecode(bitmap, uri, cryptoManager);
            Toast.makeText(this, imageManager.getRawText(), Toast.LENGTH_LONG).show();
        } else if (requestCode == getResources().getInteger(R.integer.select_public_key_case)) {
            imageManager.update(bitmap, uri);
            cryptoManager.updateEncryptCipher(cryptoManager.getKeyFrom(imageManager.getRawText()));
        } else {
            MessagingUtils.debugError("ACT_RESULT", "Wrong requestCode %d %d", requestCode, resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        for (int i : grantResults) {
            granted = granted && i == PackageManager.PERMISSION_GRANTED;
        }
        if (!granted) {
            PermissionsUtils.verifyPermissions(this, permissions);
        }
    }

    public void onUseKeyBtnClick(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, getResources().getInteger(R.integer.select_public_key_case));
    }

    public void onGenKeyBtnClick(MenuItem item) {
        PublicKey key = cryptoManager.updateDecryptCipher();
        String keyReplica = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        imageManager.update(ConversionUtils.encodeQR(keyReplica), null);
    }
}