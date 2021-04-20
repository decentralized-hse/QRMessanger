package ru.hattonuri.QRMessanger.managers;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Random;

import javax.crypto.Cipher;

import lombok.Getter;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;

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
            ContactsBook.User current = ContactsBook.getInstance().getUsers().get(name);
            if (current != null) {
                current.setKey(key);
                ContactsBook.getInstance().getUsers().put(name, current);
            } else {
                ContactsBook.getInstance().getUsers().put(name, new ContactsBook.User(new Random().nextLong(), key));
            }
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

    public void loadState() {
        if (ContactsBook.getInstance().getActiveReceiverKey() != null) {
            try {
                encryptCipher.init(Cipher.ENCRYPT_MODE, ContactsBook.getInstance().getDialer().getKey());
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
