package vn.com.smoke.clientservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.smoke.clientservice.feign.ResourceFeign;
import vn.com.smoke.common.utils.CryptUtil;
import vn.com.smoke.common.utils.RandomStringUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static vn.com.smoke.common.utils.CryptUtil.decryptData;
import static vn.com.smoke.common.utils.CryptUtil.symmetricDecrypt;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final Map<String, PublicKey> publicKeys = new HashMap<>();
    private final Map<String, PrivateKey> privateKeys = new HashMap<>();

    private final ResourceFeign resourceFeign;

    private static final String userId = RandomStringUtil.generateRandomId();


    public void registerUser() {
        Map<String, String> keys = resourceFeign.registerUser(userId);
        String publicKeyString = keys.get("publicKey");
        String privateKeyString = keys.get("privateKey");

        try {
            byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(decodedPublicKey);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicSpec);
            publicKeys.put(userId, publicKey);

            byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateSpec);
            privateKeys.put(userId, privateKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Failed to decode keys {}", e.getMessage());
        }
    }


    public String requestResource() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException, UnsupportedEncodingException {

        String generateSignature = "123";
        PublicKey publicKey = publicKeys.get(userId);

        String clientSignature = CryptUtil.encryptData(publicKey, generateSignature.getBytes());
        String encryptedResponse = resourceFeign.getResource(userId, clientSignature,
                Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        PrivateKey privateKey = privateKeys.get(userId);
        byte[] encryptedData = Base64.getDecoder().decode(encryptedResponse);
        String decryptedResponse = decryptData(encryptedData, privateKey);
        String data = symmetricDecrypt(generateSignature, decryptedResponse);
        log.info("data: {}", data);
        return data;
    }

}