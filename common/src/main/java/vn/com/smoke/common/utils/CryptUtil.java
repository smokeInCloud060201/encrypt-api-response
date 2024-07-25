package vn.com.smoke.common.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CryptUtil {
    private CryptUtil() {}
    public static final String RSA_ENCRYPT_ALGORITHM = "RSA";
    public static final String SHA_256_ENCRYPT_ALGORITHM = "SHA-256";
    public static final String AES_ENCRYPT_ALGORITHM = "AES";

    private static final Map<String, KeyPair> keyPairs = new HashMap<>();


    public static String decryptData(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(RSA_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(data);
        return new String(decryptedData);
    }

    public static String encryptData(PublicKey publicKey, byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] decryptedData = cipher.doFinal(bytes);
        return Base64.getEncoder().encodeToString(decryptedData);
    }

    public static KeyPair generateKeyPair(String userId) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ENCRYPT_ALGORITHM);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        keyPairs.put(userId, keyPair);
        return keyPair;
    }

    public static KeyPair getKeyPair(String userId) {
        return keyPairs.get(userId);
    }

    public static String symmetricEncrypt(String signatureKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKey secretKey = getKeyFromString(signatureKey);
        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String symmetricDecrypt(String signatureKey, String cryptData) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {

        SecretKey secretKey = getKeyFromString(signatureKey);

        Cipher cipher = Cipher.getInstance(AES_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(cryptData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, "UTF-8");
    }

    public static SecretKey getKeyFromString(String key) throws NoSuchAlgorithmException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance(SHA_256_ENCRYPT_ALGORITHM);
        keyBytes = sha.digest(keyBytes);
        keyBytes = Arrays.copyOf(keyBytes, 16);
        return new SecretKeySpec(keyBytes, AES_ENCRYPT_ALGORITHM);
    }
}
