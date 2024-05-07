package com.healtify.healtify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.healtify.healtify.MessageController;


@SpringBootApplication
public class HealtifyApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealtifyApplication.class, args);
		System.out.println("Backend Working");
	}

	@RestController
	public static class MainController {


		@GetMapping("/")
		public String showMessage() {
			return "Hello from Healtify Application on port 8080!";
		}
	}



}
