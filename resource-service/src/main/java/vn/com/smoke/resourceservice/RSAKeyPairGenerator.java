package vn.com.smoke.resourceservice;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RSAKeyPairGenerator  {
    private static final Map<String, KeyPair> keyPairs = new HashMap<>();

    public static KeyPair generateKeyPair(String userId) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        keyPairs.put(userId, keyPair);
        return keyPair;
    }

    public static KeyPair getKeyPair(String userId) {
        return keyPairs.get(userId);
    }
}
