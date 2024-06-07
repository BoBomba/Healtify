package com.healtify.healtify;

import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.UserProfile;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.repository.UserProfileRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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

}
