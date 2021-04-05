package ru.hattonuri.QRMessanger.managers;

import android.Manifest;
import android.content.Intent;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.NonNull;
import ru.hattonuri.QRMessanger.HistoryActivity;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.QRScannerFragment;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.utils.DialogUtils;
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

    @MenuButton(id = R.id.menu_choose_key)
    public void onChooseKeyBtnClick(MenuItem item) {
        item.getSubMenu().clear();
        for (final String name : ContactsBook.getInstance().getUsers().keySet()) {
            // Add contact
            SubMenu itemMenu = item.getSubMenu().addSubMenu(name);
            itemMenu.add(R.string.msg_accept).setOnMenuItemClickListener(item1 -> {
                ContactsBook.getInstance().setActiveReceiverKey(name);
                ContactsBook.getInstance().saveState();
                activity.getActiveReceiverManager().update();
                return true;
            });
            // Remove contact
            MenuItem removeItem = itemMenu.add(R.string.msg_remove);
            removeItem.setOnMenuItemClickListener(v -> {
                ContactsBook contacts = ContactsBook.getInstance();
                if (name.equals(contacts.getActiveReceiverKey())) {
                    contacts.setActiveReceiverKey(null);
                    activity.getActiveReceiverManager().update();
                }
                contacts.getUsers().remove(name);

                item.getSubMenu().removeItem(removeItem.getItemId());
                ContactsBook.getInstance().saveState();
                HistoryManager.getInstance().removeMessages(name);
                return true;
            });
        }
        item.getSubMenu().add(R.string.btn_reset_key_label).setOnMenuItemClickListener(item1 -> {
            ContactsBook.getInstance().setActiveReceiverKey(null);
            ContactsBook.getInstance().saveState();
            activity.getActiveReceiverManager().update();
            return true;
        });
    }

    @MenuButton(id = R.id.menu_choose_key_photo)
    public void onAddKeyFromGallery(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.add_key_gallery_case));
    }

    @MenuButton(id = R.id.menu_choose_key_scan)
    public void onAddKeyFromCamera(MenuItem item) {
        PermissionsUtils.verifyPermissions(activity, new String[]{Manifest.permission.CAMERA});
        Fragment fragment = QRScannerFragment.builder().onDecode((text) -> {
            DialogUtils.makeInputDialog(activity, activity.getResources().getString(R.string.dialog_input_name), input -> {
                CryptoManager.getInstance().updateEncryptCipher(input, ConversionUtils.getPublicKey(text));
            }, null);
            activity.getImageManager().update(text);
        }).build();
        activity.getSupportFragmentManager().beginTransaction().add(R.id.main_layout, fragment).commit();
    }

    @MenuButton(id = R.id.menu_show_receiving)
    public void onShowReceivingKey(MenuItem item) {
        if (ContactsBook.getInstance().getReceivingKey() == null) {
            CryptoManager.getInstance().updateDecryptCipher();
            String keyReplica = ConversionUtils.parseKey(ContactsBook.getInstance().getReceivingKey());
            activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
        }
        String keyReplica = ConversionUtils.parseKey(ContactsBook.getInstance().getReceivingKey());
        if (keyReplica != null) {
            activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
        }
    }

    @MenuButton(id = R.id.menu_history)
    public void onShowHistoryKey(MenuItem item) {
        Intent intent = new Intent(activity, HistoryActivity.class);
        activity.startActivity(intent);
    }
}
