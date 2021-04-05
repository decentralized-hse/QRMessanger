package ru.hattonuri.QRMessanger.managers;

import android.content.Context;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import javax.crypto.Cipher;

import lombok.Getter;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.utils.CommonUtils;
import ru.hattonuri.QRMessanger.utils.ConversionUtils;

public class CryptoManager {
    @Getter
    private static final String algorithm = KeyProperties.KEY_ALGORITHM_RSA;
    @Getter private final Integer keyLength = 2048;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private KeyPairGenerator keyPairGenerator;

    @Getter
    private static final CryptoManager instance = new CryptoManager();

    public CryptoManager() {
        try {
            encryptCipher = Cipher.getInstance(algorithm);
            decryptCipher = Cipher.getInstance(algorithm);
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keyLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEncryptCipher(String name, PublicKey key) {
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            ContactsBook.getInstance().getUsers().put(name, key);
            ContactsBook.getInstance().saveState();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void updateDecryptCipher() {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        try {
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            ContactsBook.getInstance().setPrivateKey(keyPair.getPrivate());
            ContactsBook.getInstance().setReceivingKey(keyPair.getPublic());
            ContactsBook.getInstance().saveState();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String data) {
        if (ContactsBook.getInstance().getActiveReceiverKey() == null) {
            return data;
        }
        try {
            byte[] ar = encryptCipher.doFinal(data.getBytes());
            return Base64.encodeToString(ar, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    public String decrypt(String data) {
        if (ContactsBook.getInstance().getPrivateKey() == null) {
            return data;
        }
        try {
            return new String(decryptCipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void saveState(Context context) {
//        SaveUtils.save(context, ContactsBook.getInstance(), null, "contacts.json");
//    }

    public String initConversationMessage(LaunchActivity activity) {
        if (ContactsBook.getInstance().getReceivingKey() == null) {
            CryptoManager.getInstance().updateDecryptCipher();
            String keyReplica = ConversionUtils.parseKey(ContactsBook.getInstance().getReceivingKey());
            activity.getImageManager().update(ConversionUtils.encodeQR(keyReplica), null);
            ContactsBook.getInstance().saveState();
        }
        String keyReplica = ConversionUtils.parseKey(ContactsBook.getInstance().getReceivingKey());
        if (keyReplica != null) {
            // TODO
            String msg = CryptoManager.getInstance().encrypt(CommonUtils.randomBase64(10) + keyReplica);
            activity.getImageManager().update(ConversionUtils.encodeQR(msg), null);
            return msg;
        }
        return null;
    }

    public void confirmConversationMessage(LaunchActivity activity, String message) {
        message.substring(0, 10);
        // Add contact
        // use 3 letters
    }

    public void loadState(Context context) {
//        ContactsBook.getInstance() = SaveUtils.load(context, ContactsBook.class, null, "contacts.json");
        if (ContactsBook.getInstance().getActiveReceiverKey() != null) {
            try {
                encryptCipher.init(Cipher.ENCRYPT_MODE, ContactsBook.getInstance().getDialer());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        if (ContactsBook.getInstance().getPrivateKey() != null) {
            try {
                decryptCipher.init(Cipher.DECRYPT_MODE, ContactsBook.getInstance().getPrivateKey());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }
}
