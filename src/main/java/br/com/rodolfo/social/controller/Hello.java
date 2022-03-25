package br.com.rodolfo.social.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Hello {
    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }
}
