package com.healtify.healtify;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/Healtify")
public class HealtifyController {

    @GetMapping
    public String hello(){
        return "Hello World";
    }
    
}