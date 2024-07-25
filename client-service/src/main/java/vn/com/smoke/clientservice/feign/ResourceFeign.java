package vn.com.smoke.clientservice.feign;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.com.smoke.clientservice.constants.WebConstant;

import java.util.Map;

@FeignClient(value = "resource", url = "${config.resource-host}:${config.resource-port}")
public interface ResourceFeign {

    @PostMapping("/generate-keys/{userId}")
    Map<String, String> registerUser(@PathVariable("userId") String userId);

    @PostMapping("/resource")
    String getResource(@RequestHeader("Client-Id") String clientId,@RequestHeader("Client-Signature") String clientSignature
            , @RequestHeader("Client-public") String clientPublicKey);
}
