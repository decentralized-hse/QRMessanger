package ru.hattonuri.QRMessanger.interfaces;

import android.os.Bundle;

public interface SavingState {
    void saveState(Bundle bundle);
    void loadState(Bundle bundle);
}
