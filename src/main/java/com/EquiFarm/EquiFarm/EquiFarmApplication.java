package com.EquiFarm.EquiFarm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.EquiFarm.EquiFarm.auth.AuthenticationService;
import com.EquiFarm.EquiFarm.auth.DTO.RegisterRequest;
import com.EquiFarm.EquiFarm.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
public class EquiFarmApplication {
	public static void main(String[] args) {
		SpringApplication.run(EquiFarmApplication.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunner(
			AuthenticationService service) {
		return args -> {
			var admin = RegisterRequest.builder()
					.firstName("Test")
					.lastName("User")
					.email("testuser@mail.com")
					.phoneNo("254701234567")
					.password("qwerty")
					.nationalId("123456")
					.role(Role.ADMIN)
					.build();
			System.out.println("Admin: " + service.register(admin));
		};
	}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}