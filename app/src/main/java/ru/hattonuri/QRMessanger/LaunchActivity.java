package ru.hattonuri.QRMessanger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lombok.Getter;
import ru.hattonuri.QRMessanger.managers.ActiveReceiverManager;
import ru.hattonuri.QRMessanger.managers.ActivityResultDispatcher;
import ru.hattonuri.QRMessanger.managers.CryptoManager;
import ru.hattonuri.QRMessanger.managers.ImageManager;
import ru.hattonuri.QRMessanger.managers.MenuManager;
import ru.hattonuri.QRMessanger.utils.PermissionsUtils;

public class LaunchActivity extends AppCompatActivity {
    @Getter private ActiveReceiverManager activeReceiverManager;
    @Getter private EditText editText;
    @Getter private ImageManager imageManager;
    @Getter private CryptoManager cryptoManager;
    @Getter private ActivityResultDispatcher activityResultDispatcher;
    @Getter private MenuManager menuManager;

    private void setContents() {
        activeReceiverManager = new ActiveReceiverManager(this, findViewById(R.id.active_receiver_label));
        editText = findViewById(R.id.message_edit_text);
        imageManager = new ImageManager(findViewById(R.id.imageView));
        activityResultDispatcher = new ActivityResultDispatcher(this);
        cryptoManager = new CryptoManager();
        menuManager = new MenuManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContents();
        cryptoManager.loadState(this);
        activeReceiverManager.update();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        cryptoManager.loadState(this);
        activeReceiverManager.update();
        super.onRestoreInstanceState(savedInstanceState);
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        activityResultDispatcher.dispatch(requestCode, resultCode, intent);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        menuManager.dispatch(item);
        return true;
    }
}