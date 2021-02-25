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

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private KeyPairGenerator keyPairGenerator;

    public CryptoManager() {
        try {
            contacts = new ContactsBook();
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
            contacts.getUsers().put(name, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void updateDecryptCipher() {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        try {
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            contacts.setPrivateKey(keyPair.getPrivate());
            contacts.setReceivingKey(keyPair.getPublic());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String data) {
        if (contacts.getActiveReceiverKey() == null) {
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

    public void saveState(Context context) {
        SaveUtils.save(context, contacts, null, "contacts.json");
    }

    public void loadState(Context context) {
        contacts = SaveUtils.load(context, ContactsBook.class, null, "contacts.json");
//        contacts = SaveUtils.load(bundle, "name", contacts.getClass());
        if (contacts == null) {
            contacts = new ContactsBook();
        }
        if (contacts.getActiveReceiverKey() != null) {
            try {
                encryptCipher.init(Cipher.ENCRYPT_MODE, contacts.getUsers().get(contacts.getActiveReceiverKey()));
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        if (contacts.getPrivateKey() != null) {
            try {
                decryptCipher.init(Cipher.DECRYPT_MODE, contacts.getPrivateKey());
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }
}
