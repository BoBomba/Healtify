package com.healtify.healtify;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/Healtify")
public class HealtifyController {

    @RequestMapping(method = RequestMethod.GET)
    public String hello(){
        return "Hello World";
    }
    
}