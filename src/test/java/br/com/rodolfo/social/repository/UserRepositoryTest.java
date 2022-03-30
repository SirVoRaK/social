package br.com.rodolfo.social.repository;

import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.utils.Crypt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
public class UserRepositoryTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "123456";
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void saveUser() throws NoSuchAlgorithmException {
        User user = new User(USERNAME, EMAIL, PASSWORD);
        userRepository.save(user);
    }

    @AfterEach
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void itShouldFindUserByEmailAndPassword() throws NoSuchAlgorithmException {
        String password = Crypt.sha256(PASSWORD);
        Optional<User> userFound = userRepository.findByEmailAndPassword(EMAIL, password);
        assertThat(userFound.isPresent()).isTrue();
    }

    @Test
    public void itShouldNotFindUserByEmailAndPassword() {
        Optional<User> userFound = userRepository.findByEmailAndPassword(EMAIL, PASSWORD);
        assertThat(userFound.isPresent()).isFalse();
    }

    @Test
    public void itShouldFindByEmail() {
        Optional<User> userFound = userRepository.findByEmail(EMAIL);
        assertThat(userFound.isPresent()).isTrue();
    }

    @Test
    public void itShouldNotFindByEmail() {
        Optional<User> userFound = userRepository.findByEmail("something");
        assertThat(userFound.isPresent()).isFalse();
    }

    @Test
    public void itShouldFindByUsername() {
        Optional<User> userFound = userRepository.findByUsername(USERNAME);
        assertThat(userFound.isPresent()).isTrue();
    }

    @Test
    public void itShouldNotFindByUsername() {
        Optional<User> userFound = userRepository.findByUsername("something");
        assertThat(userFound.isPresent()).isFalse();
    }
}
