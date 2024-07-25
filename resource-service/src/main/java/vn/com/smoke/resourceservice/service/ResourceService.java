package vn.com.smoke.resourceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.smoke.common.utils.CryptUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import static vn.com.smoke.common.utils.CryptUtil.symmetricEncrypt;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {


    public String getResource(String clientId, String clientSignature) throws NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyPair keyPair = CryptUtil.getKeyPair(clientId);
        if (keyPair == null) {
            log.info("User not found");
        }
        String dataToEncrypt = "Response from Resource Service";
        byte[] signature = Base64.getDecoder().decode(clientSignature);
        String signatureKey = CryptUtil.decryptData(signature, keyPair.getPrivate());

        return symmetricEncrypt(signatureKey, dataToEncrypt.getBytes());
    }

    public Map<String, String> registerUser(String userId) throws NoSuchAlgorithmException {
        log.info("User Id {} ", userId);
        KeyPair keyPair = CryptUtil.generateKeyPair(userId);
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        log.info("Register client with publicKey : {} privateKey: {}", publicKey, privateKey);
        return Map.of("publicKey", publicKey, "privateKey", privateKey);
    }
}
