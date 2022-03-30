package br.com.rodolfo.social.jwt;

import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class UserJWT {
    private final String password;
    private final UserRepository userRepository;

    public UserJWT(UserRepository userRepository) {
        this.userRepository = userRepository;
        String password;
        try {
            Dotenv dotenv = Dotenv.load();
            password = dotenv.get("JWT_PASSWORD");
        } catch (Exception e) {
            password = System.getenv("JWT_PASSWORD");
        }
        this.password = password;
    }

    public String create(String username) {
        return JWT.create().withSubject(username).sign(Algorithm.HMAC512(password));
    }

    public Optional<User> verify(String token) {
        token = token.replace("Bearer ", "");
        String username;
        try {
            username = JWT.require(Algorithm.HMAC512(password)).build().verify(token).getSubject();
        } catch (Exception e) {
            return Optional.empty();
        }
        return this.userRepository.findByUsername(username);
    }
}
