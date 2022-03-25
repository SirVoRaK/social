package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAndPassword(String email, String password);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
