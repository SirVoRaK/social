package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataMongoTest
public class UserServiceTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "123456";
    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    User user;

    @BeforeEach
    public void setUp() {
        this.userRepository.deleteAll();
        this.userService = new UserService(userRepository);
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
        this.user = this.userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        this.userRepository.deleteAll();
    }

    @Test
    public void canGetAllUsers() {
        List<User> users = userService.getUsers();
        List<User> usersFromRepository = userRepository.findAll();

        assertThat(users).isEqualTo(usersFromRepository);
    }

    @Test
    public void itShouldCreateUser() {
        User user = new User()
                .setUsername(USERNAME + "new")
                .setEmail(EMAIL + "new")
                .setPassword(PASSWORD + "new");

        userService.create(user);

        Optional<User> userOptional = userRepository.findByUsername(USERNAME + "new");

        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    public void itShouldThrowWhenUsernameIsTaken() {
        User user2 = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL + "2")
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    public void itShouldThrowWhenUsernameIsEmpty() {
        User user = new User()
                .setUsername("")
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is required");
    }

    @Test
    public void itShouldThrowWhenUsernameIsNull() {
        User user = new User()
                .setUsername(null)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsEmpty() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail("")
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsNull() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(null)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    public void itShouldThrowWhenPasswordIsEmpty() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword("");

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    public void itShouldThrowWhenPasswordIsNull() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(null);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsInvalid() {
        User user = new User()
                .setUsername(USERNAME + "new")
                .setEmail("invalidEmail") // should be something@something.something
                .setPassword(PASSWORD + "new");

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email");
    }

    @Test
    public void itShouldSignIn() throws InvalidCredentialsException {
        String token = userService.signin(user);

        assertThat(token).isNotNull();
        assertThat(token.isEmpty()).isFalse();
    }

    @Test
    public void itShouldNotSignInWhenEmailInvalid() throws InvalidCredentialsException {
        user.setEmail("invalidEmail");

        assertThatThrownBy(() -> userService.signin(user))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    public void itShouldNotSignInWhenPasswordInvalid() throws InvalidCredentialsException {
        user.setPassword("invalidPassword");

        assertThatThrownBy(() -> userService.signin(user))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    public void itShouldGetUserByToken() throws InvalidCredentialsException {
        String token = userService.signin(user);

        Optional<User> userByToken = userService.validateToken(token);

        assertThat(userByToken.isPresent()).isTrue();
    }

    @Test
    public void itShouldNotGetUserByToken() throws InvalidCredentialsException {
        String token = userService.signin(user);

        Optional<User> userByToken = userService.validateToken(token + "invalidToken");

        assertThat(userByToken.isPresent()).isFalse();
    }

    @Test
    public void itShouldUploadUserAvatar() throws InvalidCredentialsException, IOException {
        String token = userService.signin(user);

        Path path = Paths.get("src/test/resources/avatar.png");
        String name = "avatarTest.png";
        String contentType = "image/png";

        byte[] bytes = Files.readAllBytes(path);

        MultipartFile multipartFile = new MockMultipartFile(name, name, contentType, bytes);

        Optional<User> avatarUrl = userService.updateAvatar(token, multipartFile);

        assertThat(avatarUrl.isPresent()).isTrue();
        assertThat(avatarUrl.get().getAvatarUrl()).isNotNull();
        assertThat(avatarUrl.get().getAvatarUrl()).isNotEmpty();
        assertThat(avatarUrl.get().getAvatarUrl()).contains("https://natabox.s3.sa-east-1.amazonaws.com/");
    }

    @Test
    public void itShouldNotUploadUserAvatar() throws IOException, InvalidCredentialsException {
        String token = userService.signin(user);

        Path path = Paths.get("src/test/resources/avatar.png");
        String name = "avatarTest.png";
        String contentType = "image/png";

        byte[] bytes = Files.readAllBytes(path);

        MultipartFile multipartFile = new MockMultipartFile(name, name, contentType, bytes);

        Optional<User> avatarUrl = userService.updateAvatar(token + "invalidToken", multipartFile);

        assertThat(avatarUrl.isPresent()).isFalse();
    }

    @Test
    public void itShouldFindByUsername() throws NotFoundException {
        User userByUsername = userService.getByName(USERNAME);

        assertThat(userByUsername.getUsername()).isEqualTo(USERNAME);

        Optional<User> userOnRepository = userRepository.findByUsername(USERNAME);

        assertThat(userOnRepository.isPresent()).isTrue();
        assertThat(userOnRepository.get()).isEqualTo(user);
    }

    @Test
    public void itShouldNotFindByUsername() {
        assertThatThrownBy(() -> userService.getByName("invalidUsername"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
