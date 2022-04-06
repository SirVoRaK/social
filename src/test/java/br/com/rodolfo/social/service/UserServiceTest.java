package br.com.rodolfo.social.service;

import br.com.rodolfo.social.SocialApplicationTests;
import br.com.rodolfo.social.exception.ForbiddenException;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataMongoTest
public class UserServiceTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "Password@123456";
    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    User user;

    private final MultipartFile avatarImage;
    private final MultipartFile avatarText;

    public UserServiceTest() throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/avatar.png"));
        this.avatarImage = new MockMultipartFile("avatarTest.png", "avatarTest.png", "image/png", bytes);

        byte[] bytes2 = Files.readAllBytes(Path.of("src/test/resources/avatar.txt"));
        this.avatarText = new MockMultipartFile("avatarTest.txt", "avatarTest.txt", "text/plain", bytes2);
    }

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
    public void itShouldCreateUser() {
        User user = new User()
                .setUsername(USERNAME + "new")
                .setEmail(EMAIL + "new")
                .setPassword(PASSWORD + "new");

        userService.create(user, false);

        Optional<User> userOptional = userRepository.findByUsername(USERNAME + "new");

        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    public void itShouldThrowWhenUsernameIsTaken() {
        User user2 = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL + "2")
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user2, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    public void itShouldThrowWhenUsernameIsEmpty() {
        User user = new User()
                .setUsername("")
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is required");
    }

    @Test
    public void itShouldThrowWhenUsernameIsNull() {
        User user = new User()
                .setUsername(null)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsEmpty() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail("")
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsNull() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(null)
                .setPassword(PASSWORD);

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is required");
    }

    @Test
    public void itShouldThrowWhenPasswordIsEmpty() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword("");

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    public void itShouldThrowWhenPasswordIsNull() {
        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(null);

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    public void itShouldThrowWhenEmailIsInvalid() {
        User user = new User()
                .setUsername(USERNAME + "new")
                .setEmail("invalidEmail") // should be something@something.something
                .setPassword(PASSWORD + "new");

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email");
    }

    @Test
    public void itShouldThrowWhenPasswordIsInvalid() {
        User user = new User()
                .setUsername(USERNAME + "new")
                .setEmail(EMAIL + "new")
                .setPassword("invalidPassword"); // should have upper and lower case letters, numbers and special characters

        assertThatThrownBy(() -> userService.create(user, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");
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
    public void itShouldGetUserByToken() throws InvalidCredentialsException, ForbiddenException {
        String token = "Bearer " + userService.signin(user);

        User userByToken = userService.validateToken(token);

        assertThat(userByToken).isNotNull();
    }

    @Test
    public void itShouldNotGetUserByInvalidToken() throws InvalidCredentialsException {
        String token = "Bearer " + userService.signin(user);

        assertThatThrownBy(() -> userService.validateToken(token + "invalidToken"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldUploadUserAvatar() throws InvalidCredentialsException, IOException, ForbiddenException {
        String token = userService.signin(user);

        User avatarUrl = userService.updateAvatar("Bearer " + token, this.avatarImage);

        assertThat(avatarUrl.getAvatarUrl()).isNotNull();
        assertThat(avatarUrl.getAvatarUrl()).isNotEmpty();
        assertThat(avatarUrl.getAvatarUrl()).contains("https://natabox.s3.sa-east-1.amazonaws.com/");
    }

    @Test
    public void itShouldNotUploadUserAvatarWhenNotAnImage() throws InvalidCredentialsException, IOException, ForbiddenException {
        String token = userService.signin(user);

        assertThatThrownBy(() -> userService.updateAvatar("Bearer " + token, this.avatarText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("The file must be an image");
    }

    @Test
    public void itShouldNotUploadUserAvatarWithoutBearer() throws IOException, InvalidCredentialsException {
        String token = userService.signin(user);

        assertThatThrownBy(() -> userService.updateAvatar(token, this.avatarImage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotUploadUserAvatarInvalidToken() throws IOException, InvalidCredentialsException {
        String token = userService.signin(user);

        assertThatThrownBy(() -> userService.updateAvatar("Bearer " + token + "invalidToken", this.avatarImage))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
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

    @Test
    public void itShouldFollowUser() throws InvalidCredentialsException, ForbiddenException, NotFoundException {
        User userToFollow = new User()
                .setUsername("userToFollow")
                .setEmail("userToFollow@email.com")
                .setPassword("password");
        userRepository.save(userToFollow);

        assertThat(userToFollow.getFollowers().isEmpty()).isTrue();
        assertThat(user.getFollowing().isEmpty()).isTrue();

        String token = userService.signin(user);

        userService.follow("Bearer " + token, userToFollow.getUsername());

        userToFollow = userRepository.findByUsername(userToFollow.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        user = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));

        assertThat(userToFollow.getFollowers().isEmpty()).isFalse();
        assertThat(user.getFollowing().isEmpty()).isFalse();

        assertThat(userToFollow.getFollowers().contains(user.getUsername())).isTrue();
        assertThat(user.getFollowing().contains(userToFollow.getUsername())).isTrue();


        userService.follow("Bearer " + token, userToFollow.getUsername());

        userToFollow = userRepository.findByUsername(userToFollow.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        user = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));

        assertThat(userToFollow.getFollowers().isEmpty()).isTrue();
        assertThat(user.getFollowing().isEmpty()).isTrue();
    }

    @Test
    public void itShouldNotFollowItSelf() {
        assertThatThrownBy(() -> userService.follow("Bearer " + userService.signin(user), user.getUsername()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("You can't follow yourself");
    }

    @Test
    public void itShouldNotFollowUserWithoutBearer() throws InvalidCredentialsException {
        String token = userService.signin(user);
        assertThatThrownBy(() -> userService.follow(token, USERNAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotFollowUserInvalidToken() {
        assertThatThrownBy(() -> userService.follow("Bearer invalidToken", USERNAME))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotFollowUserWhenNotFound() {
        assertThatThrownBy(() -> userService.follow("Bearer " + userService.signin(user), "invalidUsername"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
