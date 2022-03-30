package br.com.rodolfo.social.service;

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
public class PostServiceTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "123456";

    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private String token;

    @BeforeEach
    public void setUp() throws InvalidCredentialsException {
        postRepository.deleteAll();
        userRepository.deleteAll();
        this.userService = new UserService(userRepository);
        this.postService = new PostService(postRepository, userService);

        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
        this.user = this.userRepository.save(user);
        this.token = this.userService.signin(user);
    }

    @AfterEach
    public void tearDown() {
        this.postRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void itShouldCreateAPost() {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThat(post.getAuthor().getPassword()).isNull();
        assertThat(post.getAuthor().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getMessage()).isEqualTo(content);
        assertThat(postRepository.findById(post.getId()).isPresent()).isTrue();
        assertThat(postRepository.findById(post.getId()).get().getId()).isEqualTo(post.getId());
    }

    @Test
    public void itShouldNotCreateAPostEmptyContent() {
        String content = "";
        assertThatThrownBy(() -> this.postService.create("Bearer " + token, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldNotCreateAPostNullContent() {
        String content = null;
        assertThatThrownBy(() -> this.postService.create("Bearer " + token, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldNotCreateAPostTokenWithoutBearer() {
        String content = "Test content";
        assertThatThrownBy(() -> this.postService.create(token, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header must start with 'Bearer '");
    }

    @Test
    public void itShouldNotCreateAPostInvalidToken() {
        String content = "Test content";
        assertThatThrownBy(() -> this.postService.create("Bearer " + token + "invalidToken", content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotCreateAPostWithBearerAndWithoutToken() {
        String content = "Test content";
        assertThatThrownBy(() -> this.postService.create("Bearer ", content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldGetPostById() throws NotFoundException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        Post result = this.postService.get(post.getId());
        assertThat(result.getId()).isEqualTo(post.getId());
        assertThat(result.getMessage()).isEqualTo(post.getMessage());
        assertThat(result.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(result.getAuthor().getPassword()).isNull();
    }

    @Test
    public void itShouldNotGetPostById() throws NotFoundException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.get(post.getId() + "invalidId"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    public void itShouldGetByAuthorName() throws NotFoundException, InvalidCredentialsException {
        int size = 10;

        for (int i = 0; i < size; i++) {
            String content = "Test content " + i;
            this.postService.create("Bearer " + token, content);
        }
        User anotherUser = new User()
                .setUsername("anotherUser")
                .setEmail("another@email.com")
                .setPassword("anotherPassword");
        this.userRepository.save(anotherUser);
        String anotherToken = this.userService.signin(anotherUser);
        for (int i = 0; i < size; i++) {
            String content = "Another Test content " + i;
            this.postService.create("Bearer " + anotherToken, content);
        }

        List<Post> anotherUserPosts = this.postService.getByAuthorName("anotherUser", 0, 1000);
        List<Post> userPosts = this.postService.getByAuthorName(user.getUsername(), 0, 1000);

        assertThat(anotherUserPosts.size()).isEqualTo(size);
        assertThat(userPosts.size()).isEqualTo(size);

        for (Post post : anotherUserPosts) {
            assertThat(post.getAuthor().getUsername()).isEqualTo("anotherUser");
            assertThat(post.getAuthor().getPassword()).isNull();

            for (User likeUser : post.getLikes()) {
                assertThat(likeUser.getPassword()).isNull();
            }
        }

        for (Post post : userPosts) {
            assertThat(post.getAuthor().getUsername()).isEqualTo(user.getUsername());
            assertThat(post.getAuthor().getPassword()).isNull();

            for (User likeUser : post.getLikes()) {
                assertThat(likeUser.getPassword()).isNull();
            }
        }
    }

    @Test
    public void itShouldDeletePost() throws NotFoundException, ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        Post deletedPost = this.postService.delete("Bearer " + this.token, post.getId());
        assertThatThrownBy(() -> this.postService.get(post.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
        assertThat(deletedPost.getId()).isEqualTo(post.getId());
    }

    @Test
    public void itShouldNotDeletePostTokenWithoutBearer() {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete(token, post.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Authorization header must start with 'Bearer '");
    }

    @Test
    public void itShouldNotDeletePostInvalidToken() {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + token + "invalidToken", post.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotDeletePostNotFound() {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + token, post.getId() + "invalidId"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    public void itShouldNotDeletePostNotAuthor() throws InvalidCredentialsException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        User anotherUser = new User()
                .setUsername("anotherUser")
                .setEmail("another@email.com")
                .setPassword("anotherPassword");
        this.userRepository.save(anotherUser);
        String anotherToken = this.userService.signin(anotherUser);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + anotherToken, post.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("You are not the author of this post");
    }
}
