package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.UserDto;
import br.com.rodolfo.social.forms.UserForm;
import br.com.rodolfo.social.forms.UserSigninForm;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping
    public List<User> getAll() {
        return this.userService.getUsers();
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> create(@RequestBody UserForm user, UriComponentsBuilder uriBuilder) {
        User saved = this.userService.create(user.convert());
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDto(saved));
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDto> signin(@RequestBody UserSigninForm user) throws Exception {
        User saved = this.userService.signin(user.convert());
        if (saved == null) {
            throw new Exception("Usuário ou senha inválidos");
        }
        return ResponseEntity.ok(new UserDto(saved));
    }
}
