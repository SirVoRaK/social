package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.dto.Info;
import br.com.rodolfo.social.dto.UserDto;
import br.com.rodolfo.social.dto.UserSignupDto;
import br.com.rodolfo.social.dto.UserTokenDto;
import br.com.rodolfo.social.exception.*;
import br.com.rodolfo.social.forms.UserForgotPasswordForm;
import br.com.rodolfo.social.forms.UserForm;
import br.com.rodolfo.social.forms.UserResetPasswordForm;
import br.com.rodolfo.social.forms.UserSigninForm;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.service.UserService;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.mail.MessagingException;
import java.net.URI;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(description = "Created", responseCode = "201")
    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<UserSignupDto> create(@RequestBody UserForm user, UriComponentsBuilder uriBuilder) {
        User saved = this.userService.create(user.convert(), true);
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserSignupDto(saved));
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Logged in", responseCode = "200")
    @ApiResponse(description = "Invalid email or password", responseCode = "401", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<UserTokenDto> signin(@RequestBody UserSigninForm user) throws Exception {
        String token = this.userService.signin(user.convert());
        return ResponseEntity.ok(new UserTokenDto(token));
    }

    @GetMapping("/signin/token")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Valid token", responseCode = "200")
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<UserDto> verifyToken(@RequestHeader("Authorization") String token) throws InvalidCredentialsException, TokenExpiredException, ForbiddenException {
        User user = this.userService.validateToken(token);
        return ResponseEntity.ok(new UserDto(user));
    }

    @PatchMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Changed avatar", responseCode = "200")
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<UserDto> updateAvatar(@RequestHeader("Authorization") String token, @RequestParam(value = "avatar", required = false) MultipartFile file) throws ForbiddenException {
        User user = this.userService.updateAvatar(token, file);
        return ResponseEntity.ok(new UserDto(user));
    }

    @PatchMapping("/{username}/follow")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(description = "Success", responseCode = "200")
    @ApiResponse(description = "Cannot follow yourself", responseCode = "401", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Invalid token", responseCode = "403", content = @Content(schema = @Schema(implementation = SpringException.class)))
    @ApiResponse(description = "Not found", responseCode = "404", content = @Content(schema = @Schema(implementation = SpringException.class)))
    public ResponseEntity<UserDto> follow(@RequestHeader("Authorization") String token, @PathVariable("username") String username) throws ForbiddenException, NotFoundException, UnauthorizedException {
        User changedUser = this.userService.follow(token, username);
        return ResponseEntity.ok(new UserDto(changedUser));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Info> forgotPassword(@RequestBody UserForgotPasswordForm user) throws NotFoundException, MessagingException {
        this.userService.forgotPassword(user);
        return ResponseEntity.ok(new Info("Email sent"));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Info> resetPassword(@RequestBody UserResetPasswordForm user) throws NotFoundException, InvalidCredentialsException {
        this.userService.resetPassword(user);
        return ResponseEntity.ok(new Info("Password changed"));
    }
}
