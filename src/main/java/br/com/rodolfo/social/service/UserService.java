package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.jwt.UserJWT;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private UserJWT userJWT;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User create(User user) throws IllegalArgumentException {
        if (this.findByUsername(user.getUsername()).isPresent())
            throw new IllegalArgumentException("Username already taken");
        if (this.findByEmail(user.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already taken");
        if (!this.isEmailValid(user.getEmail()))
            throw new IllegalArgumentException("Invalid email");

        return userRepository.save(user);
    }

    private Boolean isEmailValid(String email) {
        return Pattern.matches("[a-zA-Z_.0-9]+@[a-zA-Z_.0-9]+\\.[a-zA-Z_.0-9]+", email);
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String signin(User user) throws InvalidCredentialsException {
        Optional<User> userOptional = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty())
            throw new InvalidCredentialsException("Invalid email or password");

        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        return this.userJWT.create(userOptional.get().getUsername());
    }

    public Optional<User> validateToken(String token) {
        if (userJWT == null)
            this.userJWT = new UserJWT(this.userRepository);

        return this.userJWT.verify(token);
    }
}
