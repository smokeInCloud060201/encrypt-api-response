package vn.com.smoke.clientservice.util;

import java.security.SecureRandom;


public class RandomStringUtil {
    private RandomStringUtil() {}
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString() {
        return generateDigits(10);
    }

    public static String generateRandomId() {
        return generateDigits(10);
    }

    private static String generateDigits(int length) {
        StringBuilder digits = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.toString();
    }

    private static String generateAlphabets(int length) {
        StringBuilder alphabets = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            alphabets.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return alphabets.toString();
    }
}
