package ru.hattonuri.QRMessanger.managers;

import android.content.Intent;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PublicKey;

import lombok.NonNull;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.QRScannerFrament;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.annotations.MenuButton;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
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

    @MenuButton(id = R.id.btn_choose_key_photo)
    public void onAddKeyFromGallery(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.add_key_gallery_case));
    }

    @MenuButton(id = R.id.btn_gen_key)
    public void onGenKeyBtnClick(MenuItem item) {
        PublicKey key = activity.getCryptoManager().updateDecryptCipher();
        String keyReplica = ConversionUtils.parseKey(key);
        activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
        activity.getCryptoManager().saveState(activity);
    }

    @MenuButton(id = R.id.btn_choose_key)
    public void onChooseKeyBtnClick(MenuItem item) {
        item.getSubMenu().clear();
        for (final String name : activity.getCryptoManager().getContacts().getUsers().keySet()) {
            SubMenu itemMenu = item.getSubMenu().addSubMenu(name);
            itemMenu.add(R.string.msg_accept).setOnMenuItemClickListener(item1 -> {
                activity.getCryptoManager().getContacts().setActiveReceiverKey(
                        activity.getCryptoManager().getContacts().getUsers().get(name)
                );
                activity.getCryptoManager().saveState(activity);
                return true;
            });
            final MenuItem removeItem = itemMenu.add(R.string.msg_remove);
            removeItem.setOnMenuItemClickListener(v -> {
                ContactsBook contacts = activity.getCryptoManager().getContacts();
                if (contacts.getUsers().get(name) == contacts.getActiveReceiverKey()) {
                    contacts.setActiveReceiverKey(null);
                }
                contacts.getUsers().remove(name);

                item.getSubMenu().removeItem(removeItem.getItemId());
                activity.getCryptoManager().saveState(activity);
                return true;
            });
        }
        item.getSubMenu().add(R.string.btn_reset_key).setOnMenuItemClickListener(item12 -> {
            activity.getCryptoManager().getContacts().setActiveReceiverKey(null);
            activity.getCryptoManager().saveState(activity);
            return true;
        });
    }

    //TODO Add update with text
    @MenuButton(id = R.id.btn_choose_key_scan)
    public void onAddKeyFromCamera(MenuItem item) {
        Fragment fragment = QRScannerFrament.builder().onDecode((text) -> activity.getImageManager().update(ConversionUtils.encodeQR(text), null)).build();
        activity.getSupportFragmentManager().beginTransaction().
                add(R.id.main_layout, fragment).commit();
//        try {
//            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
//            activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.add_key_camera_case));
//        } catch (Exception e) {
//            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
//            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//            activity.startActivity(marketIntent);
//        }
    }
}
