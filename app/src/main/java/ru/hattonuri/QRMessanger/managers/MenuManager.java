package ru.hattonuri.QRMessanger.managers;

import android.Manifest;
import android.content.Intent;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.NonNull;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.QRScannerFragment;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.RequireInputDialog;
import ru.hattonuri.QRMessanger.annotations.MenuButton;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;
import ru.hattonuri.QRMessanger.utils.PermissionsUtils;

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
        activity.getCryptoManager().updateDecryptCipher();
        String keyReplica = ConversionUtils.parseKey(activity.getCryptoManager().getContacts().getReceivingKey());
        activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
        activity.getCryptoManager().saveState(activity);
    }

    @MenuButton(id = R.id.btn_choose_key)
    public void onChooseKeyBtnClick(MenuItem item) {
        item.getSubMenu().clear();
        for (final String name : activity.getCryptoManager().getContacts().getUsers().keySet()) {
            SubMenu itemMenu = item.getSubMenu().addSubMenu(name);
            itemMenu.add(R.string.msg_accept).setOnMenuItemClickListener(item1 -> {
                activity.getCryptoManager().getContacts().setActiveReceiverKey(name);
                activity.getCryptoManager().saveState(activity);
                activity.getActiveReceiverManager().update();
                return true;
            });
            final MenuItem removeItem = itemMenu.add(R.string.msg_remove);
            removeItem.setOnMenuItemClickListener(v -> {
                ContactsBook contacts = activity.getCryptoManager().getContacts();
                if (name.equals(contacts.getActiveReceiverKey())) {
                    contacts.setActiveReceiverKey(null);
                    activity.getActiveReceiverManager().update();
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
            activity.getActiveReceiverManager().update();
            return true;
        });
    }

    @MenuButton(id = R.id.btn_choose_key_scan)
    public void onAddKeyFromCamera(MenuItem item) {
        PermissionsUtils.verifyPermissions(activity, new String[]{Manifest.permission.CAMERA});
        Fragment fragment = QRScannerFragment.builder().onDecode((text) -> {
            RequireInputDialog.makeDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
                activity.getCryptoManager().updateEncryptCipher(input, ConversionUtils.getPublicKey(text));
                activity.getCryptoManager().saveState(activity);
            });
            activity.getImageManager().update(text);
        }).build();
        activity.getSupportFragmentManager().beginTransaction().add(R.id.main_layout, fragment).commit();
    }

    @MenuButton(id = R.id.btn_show_receiving)
    public void onShowReceivingKey(MenuItem item) {
        String keyReplica = ConversionUtils.parseKey(activity.getCryptoManager().getContacts().getReceivingKey());
        if (keyReplica != null) {
            activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
        }
    }
}
