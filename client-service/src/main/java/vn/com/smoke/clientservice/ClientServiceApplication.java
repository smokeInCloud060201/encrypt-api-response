package vn.com.smoke.clientservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import vn.com.smoke.clientservice.service.UserService;

@SpringBootApplication
@Slf4j
@EnableFeignClients
public class ClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner run(UserService userService) {
		return args -> {
			try {
				userService.registerUser();
			} catch (RuntimeException e) {
				log.info("Got the error following exception: {}", e.getMessage());
			}
		};
	}

}
