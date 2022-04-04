package br.com.rodolfo.social.service;

import br.com.rodolfo.social.SocialApplicationTests;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.PostRepository;
import br.com.rodolfo.social.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataMongoTest
public class FeedServiceTest {
    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    private PostRepository postRepository;
    private PostService postService;

    private FeedService feedService;

    User user;
    String token;


    @BeforeEach
    public void setUp() throws InvalidCredentialsException {
        postRepository.deleteAll();
        userRepository.deleteAll();

        userService = new UserService(userRepository);
        postService = new PostService(postRepository, userService);
        feedService = new FeedService(postService, userService);

        User user = new User()
                .setUsername("Test")
                .setEmail("test@test.test")
                .setPassword("Password@123");
        this.user = this.userService.create(user, false);
        this.token = this.userService.signin(user);
    }

    @AfterEach
    public void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void itShouldGetTheFeed() throws ForbiddenException, NotFoundException, InvalidCredentialsException {
        String password = "Password@123";
        User user1 = new User()
                .setUsername("user1")
                .setEmail("user1@email.com")
                .setPassword(password);
        User user2 = new User()
                .setUsername("user2")
                .setEmail("user2@email.com")
                .setPassword(password);
        User user3 = new User()
                .setUsername("user3")
                .setEmail("user3@email.com")
                .setPassword(password);

        userService.create(user1, false);
        userService.create(user2, false);
        userService.create(user3, false);

        userService.follow("Bearer " + token, user1.getUsername());
        userService.follow("Bearer " + token, user2.getUsername());
        userService.follow("Bearer " + token, user3.getUsername());

        String tokenUser1 = userService.signin(user1);
        String tokenUser2 = userService.signin(user2);
        String tokenUser3 = userService.signin(user3);

        postService.create("Bearer " + tokenUser1, "Post 1");
        postService.create("Bearer " + tokenUser2, "Post 2");
        postService.create("Bearer " + tokenUser3, "Post 3");

        List<Post> posts = feedService.getFeed("Bearer " + token);

        assertThat(posts.size()).isEqualTo(3);
    }

    @Test
    public void itShouldNotReturnPostsWithoutBearer() {
        assertThatThrownBy(() -> feedService.getFeed(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotReturnPostsWithInvalidToken() {
        assertThatThrownBy(() -> feedService.getFeed("Bearer " + token + "invalidToken"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }
}
