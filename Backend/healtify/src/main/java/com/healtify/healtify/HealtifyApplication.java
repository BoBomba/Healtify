package com.healtify.healtify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
		origins = {
				"http://localhost:3000",
				"https://staging.example.com",
				"https://app.example.com"
		},
		methods = {
				RequestMethod.OPTIONS,
				RequestMethod.GET,
				RequestMethod.PUT,
				RequestMethod.DELETE,
				RequestMethod.POST
		})

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

//	 @RestController
//	 @CrossOrigin(origins = "http://localhost:3000")
//	 public static class registerController {
//
//	 	@GetMapping("/register")
//	 	public String registerUser() {
//	 		return "Register User";
//	 	}
//	 }

	public static void main(String[] args) {
		SpringApplication.run(HealtifyApplication.class, args);
		System.out.println("Backend Working...");
	}

}
