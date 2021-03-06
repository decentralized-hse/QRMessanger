package ru.hattonuri.QRMessanger.managers;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Objects;

import javax.crypto.Cipher;

import lombok.Getter;
import ru.hattonuri.QRMessanger.LaunchActivity;
import ru.hattonuri.QRMessanger.R;
import ru.hattonuri.QRMessanger.groupStructures.ContactsBook;
import ru.hattonuri.QRMessanger.utils.CommonUtils;
import ru.hattonuri.QRMessanger.utils.MessagingUtils;

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

    public void addContact(String name, PublicKey key) {
        addContact(name, key, CommonUtils.generateRandomString(LaunchActivity.getInstance().getResources().getInteger(R.integer.identity_key_len)));
    }

    public void addContact(String name, PublicKey key, String uuid) {
        ContactsBook.User current = ContactsBook.getInstance().getUsers().get(name);
        if (current != null) {
            current.setKey(key);
        } else {
            MessagingUtils.debugError("ADD CONTACT UUID", uuid);
            ContactsBook.getInstance().getUsers().put(name, new ContactsBook.User(uuid, key));
        }
        ContactsBook.getInstance().saveState();
    }

    public void updateEncryptCipher(String name) {
        try {
            PublicKey key = Objects.requireNonNull(ContactsBook.getInstance().getUsers().get(name)).getKey();
            if (key != null) {
                encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            }
            ContactsBook.getInstance().setActiveReceiverKey(name);
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
                if (ContactsBook.getInstance().getDialer().getKey() != null) {
                    encryptCipher.init(Cipher.ENCRYPT_MODE, ContactsBook.getInstance().getDialer().getKey());
                }
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
