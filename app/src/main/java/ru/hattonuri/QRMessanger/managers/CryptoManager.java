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
import lombok.Setter;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.utils.SaveUtils;

public class CryptoManager {
    @Getter private final String algorithm = KeyProperties.KEY_ALGORITHM_RSA;
    @Getter private final Integer keyLength = 2048;

    @Getter @Setter
    private ContactsBook contacts;

    private final String saveFileName = "contacts.json";
    private final Context context;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private KeyPairGenerator keyPairGenerator;

    public CryptoManager(Context context) {
        this.context = context;
        try {
            encryptCipher = Cipher.getInstance(algorithm);
            decryptCipher = Cipher.getInstance(algorithm);
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keyLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadState();
    }

    public void updateEncryptCipher(String name, PublicKey key) {
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            contacts.getUsers().put(name, key);
            saveState();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public PublicKey updateDecryptCipher() {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        try {
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            contacts.setPrivateKey(keyPair.getPrivate());
            saveState();
            return keyPair.getPublic();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(String data) {
        if (contacts.getActiveReceiver() == null) {
            return data;
        }
        try {
            byte[] ar = encryptCipher.doFinal(data.getBytes());
            return Base64.encodeToString(ar, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String data) {
        if (contacts.getPrivateKey() == null) {
            return data;
        }
        try {
            return new String(decryptCipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveState() {
        SaveUtils.save(context, contacts, null, saveFileName);
    }

    public void loadState() {
        contacts = SaveUtils.load(context, ContactsBook.class, null, saveFileName);
    }
}
