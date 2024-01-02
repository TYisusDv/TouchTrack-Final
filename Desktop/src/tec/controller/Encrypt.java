package tec.controller;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encrypt {
    public static String encrypt(byte[] fingerprintEnBytes, String clave) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(clave.toCharArray(), "".getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretPassword = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher Encryptor = Cipher.getInstance("AES");
        Encryptor.init(Cipher.ENCRYPT_MODE, secretPassword);

        String fingerprintBase64 = Base64.getEncoder().encodeToString(fingerprintEnBytes);

        byte[] fingerprintEncrypted = Encryptor.doFinal(fingerprintBase64.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(fingerprintEncrypted);
    }

    public static byte[] decrypt(String fingerprintEncriptada, String clave) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(clave.toCharArray(), "".getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secretPassword = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher Encryptor = Cipher.getInstance("AES");
        Encryptor.init(Cipher.DECRYPT_MODE, secretPassword);

        byte[] fingerprintEncryptedBytes = Base64.getDecoder().decode(fingerprintEncriptada);

        String fingerprintBase64 = new String(Encryptor.doFinal(fingerprintEncryptedBytes), "UTF-8");

        byte[] fingerprintDecrypted = Base64.getDecoder().decode(fingerprintBase64);

        return fingerprintDecrypted;
    }

}