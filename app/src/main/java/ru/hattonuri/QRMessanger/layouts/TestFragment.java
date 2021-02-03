package ru.hattonuri.QRMessanger.layouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;

public class TestFragment extends Fragment {
    public static TestFragment newInstance(String textToShow) {
        Bundle args = new Bundle();
        args.putCharSequence("textToShow", textToShow);
        TestFragment instance = new TestFragment();
        instance.setArguments(args);
        return instance;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View textView = inflater.inflate(R.layout.test_layout, container, false);
//        textView.setText(savedInstanceState.getCharSequence("textToShow"));
        MessagingUtils.debugToast(getContext(), "...");
        return textView;
    }
}
