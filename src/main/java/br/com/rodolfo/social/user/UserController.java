package br.com.rodolfo.social.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @RequestMapping("/")
    public String index() {
        return "Hello World";
    }

}
