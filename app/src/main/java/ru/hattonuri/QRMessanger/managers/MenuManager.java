package ru.hattonuri.QRMessanger.managers;

import android.content.Intent;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PublicKey;

import lombok.NonNull;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.annotations.MenuButton;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;

public class MenuManager {
    private final LaunchActivity activity;

    public MenuManager(LaunchActivity activity) {
        this.activity = activity;
    }

    public void dispatch(@NonNull MenuItem item) {
        for (Method method : this.getClass().getDeclaredMethods()) {
            MenuButton annotation = method.getAnnotation(MenuButton.class);
            if (annotation != null && annotation.id() == item.getItemId()) {
                try {
                    method.invoke(this, item);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @MenuButton(id = R.id.btn_add_key)
    public void onAddKeyBtnClick(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.add_key_case));
    }

    @MenuButton(id = R.id.btn_gen_key)
    public void onGenKeyBtnClick(MenuItem item) {
        PublicKey key = activity.getCryptoManager().updateDecryptCipher();
        String keyReplica = ConversionUtils.parseKey(key);
        activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
    }

    @MenuButton(id = R.id.btn_choose_key)
    public void onChooseKeyBtnClick(MenuItem item) {
        item.getSubMenu().clear();
        for (String i : activity.getCryptoManager().getContacts().getUsers().keySet()) {
            final String name = i;
            item.getSubMenu().add(i).setOnMenuItemClickListener(item1 -> {
                activity.getCryptoManager().getContacts().setActiveReceiverKey(
                        activity.getCryptoManager().getContacts().getUsers().get(name)
                );
                return false;
            });
        }
    }
}