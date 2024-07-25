package vn.com.smoke.resourceservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import vn.com.smoke.resourceservice.service.ResourceService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/generate-keys/{userId}")
    public Map<String, String> generateKeys(@PathVariable String userId) throws NoSuchAlgorithmException {
        return resourceService.registerUser(userId);
    }

    @PostMapping("/resource")
    public String processRequest(@RequestHeader("Client-Id") String clientId, @RequestHeader("Client-Signature") String clientSignature) throws NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        return resourceService.getResource(clientId, clientSignature);
    }
}
