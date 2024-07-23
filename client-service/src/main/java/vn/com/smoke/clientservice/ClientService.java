package vn.com.smoke.clientservice;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class ClientService {
    private final RestTemplate restTemplate;
    private final Map<String, PublicKey> publicKeys = new HashMap<>();
    private final Map<String, PrivateKey> privateKeys = new HashMap<>();

    public ClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void registerUser(String resourceServiceUrl, String userId) {
        Map<String, String> keys = restTemplate.postForObject(resourceServiceUrl + "/generate-keys/" + userId, null, Map.class);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestResource(String resourceServiceUrl, String userId) {
        //TODO Generate a random key each request. => encrypt the key and send it to resource service
        // Resource service decrypt key and use the key to decrypt the response using the Symmetric ciphers
        // => Performance
        try {
            PublicKey publicKey = publicKeys.get(userId);
            System.out.println("Public key " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            // Prepare request data
            Map<String, String> requestData = new HashMap<>();
            requestData.put("publicKey", Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            // Send public key to resource service and get encrypted response
            String encryptedResponse = restTemplate.postForObject(resourceServiceUrl + "/process/" + userId, requestData, String.class);

            // Decrypt the response using the user's private key
            PrivateKey privateKey = privateKeys.get(userId);
            byte[] encryptedData = Base64.getDecoder().decode(encryptedResponse);
            String decryptedResponse = decryptData(encryptedData, privateKey);

            System.out.println("Decrypted Response: " + decryptedResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String decryptData(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(data);
        return new String(decryptedData);
    }
}