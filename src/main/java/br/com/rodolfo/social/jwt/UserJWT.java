package br.com.rodolfo.social.jwt;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.cdimascio.dotenv.Dotenv;

public class UserJWT {
    private final String password;
    private final UserRepository userRepository;

    public UserJWT(UserRepository userRepository) {
        this.userRepository = userRepository;
        String jwtPassword;
        try {
            Dotenv dotenv = Dotenv.load();
            jwtPassword = dotenv.get("JWT_PASSWORD");
        } catch (Exception e) {
            jwtPassword = System.getenv("JWT_PASSWORD");
        }
        this.password = jwtPassword;
    }

    public String create(String username) {
        return JWT.create().withSubject(username).sign(Algorithm.HMAC512(password));
    }

    public User verify(String token) throws ForbiddenException {
        token = token.replace("Bearer ", "");
        String username;
        try {
            username = JWT.require(Algorithm.HMAC512(password)).build().verify(token).getSubject();
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }
        return this.userRepository.findByUsername(username).orElseThrow(() -> new ForbiddenException("Invalid token"));
    }
}
