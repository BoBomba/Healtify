package com.healtify.healtify;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
@CrossOrigin(origins = "http://localhost:3000") // Zmień na adres URL swojej aplikacji React
public class MessageController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }

    @GetMapping("/message")
    public String showMessage(String message) {
        return message;
    }
    
    @PostMapping("/message")
    public String receiveMessage(@RequestBody String message) {
        // Tutaj możesz przetworzyć otrzymaną wiadomość
        showMessage(message);
        message = message.toUpperCase();
        return "Received message: " + message;
    }
}
