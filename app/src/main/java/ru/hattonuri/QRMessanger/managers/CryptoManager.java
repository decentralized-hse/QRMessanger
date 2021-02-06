package ru.hattonuri.QRMessanger.managers;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import lombok.Getter;

public class CryptoManager {
    @Getter
    private final String algorithm = KeyProperties.KEY_ALGORITHM_RSA;
    @Getter
    private final Integer keyLength = 2048;

    private PrivateKey decryptKey;
    private PublicKey encryptKey;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    private KeyPairGenerator keyPairGenerator;
//    private KeyStore keyStore;
    private KeyFactory keyFactory;

    public CryptoManager() {
        try {
            encryptCipher = Cipher.getInstance(algorithm);
            decryptCipher = Cipher.getInstance(algorithm);

//            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
            keyFactory = KeyFactory.getInstance(algorithm);
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(keyLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEncryptCipher(PublicKey key) {
        if (key == null) {
            return;
        }
        try {
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            encryptKey = key;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public PublicKey updateDecryptCipher() {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        try {
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            decryptKey = keyPair.getPrivate();
            return keyPair.getPublic();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(String data) {
        if (encryptKey == null) {
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
        if (decryptKey == null) {
            return data;
        }
        try {
            return new String(decryptCipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getKeyFrom(String data) {
        if (data == null) {
            return null;
        }
        try {
            return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(data, Base64.DEFAULT)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
