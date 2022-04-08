package br.com.rodolfo.social.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Hidden
public class Hello {
    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }
}
