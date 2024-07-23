package vn.com.smoke.resourceservice;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@RestController
public class ResourceController {

    @PostMapping("/generate-keys/{userId}")
    public Map<String, String> generateKeys(@PathVariable String userId) {
        try {
            KeyPair keyPair = RSAKeyPairGenerator.generateKeyPair(userId);
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return Map.of("publicKey", publicKey, "privateKey", privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Error generating keys");
        }
    }

    @PostMapping("/process/{userId}")
    public String processRequest(@PathVariable String userId, @RequestBody Map<String, String> request) {
        try {
            String publicKeyString = request.get("publicKey");

            // Retrieve the user's key pair
            KeyPair keyPair = RSAKeyPairGenerator.getKeyPair(userId);
            if (keyPair == null) {
                return "User not found";
            }

            // Decode the received public key
            byte[] decodedPublicKey = Base64.getDecoder().decode(publicKeyString);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decodedPublicKey));

            // Data to be encrypted and sent back to the client
            String dataToEncrypt = "Response from Resource Service";

            // Encrypt the data using the client's public key
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());

            // Return the encrypted data
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing request";
        }
    }
}
