package vn.com.smoke.clientservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner run(ClientService clientService) {
		return args -> {
			String resourceServiceUrl = "http://localhost:8080";
			String userId = "user1";

			// Register user and get key pairs
			clientService.registerUser(resourceServiceUrl, userId);

			// Request resource using the user's public key
			clientService.requestResource(resourceServiceUrl, userId);
		};
	}

}
