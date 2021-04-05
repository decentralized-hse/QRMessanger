package ru.hattonuri.QRMessanger;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_share);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Uri uri = Uri.parse(b.getString("image"));
            ImageView imageView = findViewById(R.id.imageShareStopView);
            imageView.setImageURI(uri);
        }
    }

    public void onCodeFullScreenClick(View view) {
        finish();
    }
}