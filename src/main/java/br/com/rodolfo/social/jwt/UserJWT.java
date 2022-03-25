package br.com.rodolfo.social.jwt;

import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Optional;

public class UserJWT {
    private String password = "senhateste123";
    private final UserRepository userRepository;

    public UserJWT(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String create(String username) {
        return JWT.create().withSubject(username).sign(Algorithm.HMAC512(password));
    }

    public Optional<User> verify(String token) {
        token = token.replace("Bearer ", "");
        String username = JWT.require(Algorithm.HMAC512(password)).build().verify(token).getSubject();
        return this.userRepository.findByUsername(username);
    }
}
