package br.com.rodolfo.social.service;

import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User create(User user) throws IllegalArgumentException {
        if (this.findByUsername(user.getUsername()).isEmpty())
            return userRepository.save(user);

        throw new IllegalArgumentException("Username already taken");
    }

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User signin(User user) {
        Optional<User> userOptional = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        return userOptional.orElse(null);
    }
}
