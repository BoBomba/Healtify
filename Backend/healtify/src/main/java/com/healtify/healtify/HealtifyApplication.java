package com.healtify.healtify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.healtify.healtify.registerController;


@SpringBootApplication
public class HealtifyApplication {

	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@RestController
	public static class MainController {


		@GetMapping("/")
		public String showMessage() {
			return "Hello from Healtify Application on port 8080!";
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(HealtifyApplication.class, args);
		System.out.println("Backend Working...");
	}

	 @RestController
	 @CrossOrigin(origins = "http://localhost:3000")
	 public static class registerController {

	 	@GetMapping("/register")
	 	public String registerUser() {
	 		return "Register User";
	 	}
	 }

}
