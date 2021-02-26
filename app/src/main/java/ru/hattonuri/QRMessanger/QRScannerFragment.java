package ru.hattonuri.QRMessanger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import lombok.Builder;
import ru.hattonuri.QRMessanger.interfaces.InputProcess;

@Builder
public class QRScannerFragment extends Fragment {
    private CodeScanner mCodeScanner;
    private final InputProcess onDecode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LaunchActivity activity = (LaunchActivity) getActivity();
        activity.setContentsVisibility(View.INVISIBLE);
        View root = inflater.inflate(R.layout.layout_qrscanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        QRScannerFragment scannerFragment = this;
        mCodeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            // TODO Make normal call for remove)
            ((LaunchActivity) activity).getSupportFragmentManager().beginTransaction().remove(scannerFragment).commit();
            mCodeScanner.releaseResources();
            activity.setContentsVisibility(View.VISIBLE);
            onDecode.run(result.getText());
        }));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
