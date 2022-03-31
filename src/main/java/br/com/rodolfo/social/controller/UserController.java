package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.UserDto;
import br.com.rodolfo.social.dto.UserTokenDto;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.forms.UserForm;
import br.com.rodolfo.social.forms.UserSigninForm;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.service.UserService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<UserTokenDto> signin(@RequestBody UserSigninForm user) throws Exception {
        String token = this.userService.signin(user.convert());
        return ResponseEntity.ok(new UserTokenDto(token));
    }

    @GetMapping("/signin/token")
    public ResponseEntity<User> verifyToken(@RequestHeader("Authorization") String token) throws InvalidCredentialsException, TokenExpiredException {
        if (token == null || token.isEmpty())
            throw new InvalidCredentialsException("Missing token in request header");

        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Authorization header needs to start with Bearer");

        Optional<User> user = this.userService.validateToken(token);
        if (user.isEmpty())
            throw new InvalidCredentialsException("Invalid token");
        return ResponseEntity.ok(user.get());
    }

    @PatchMapping("/avatar")
    public ResponseEntity<User> updateAvatar(@RequestHeader("Authorization") String token, @RequestParam("avatar") MultipartFile file) throws ForbiddenException {
        User user = this.userService.updateAvatar(token, file);
        return ResponseEntity.ok(user);
    }
}
