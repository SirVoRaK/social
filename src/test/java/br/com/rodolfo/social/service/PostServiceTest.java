package br.com.rodolfo.social.service;

import br.com.rodolfo.social.SocialApplicationTests;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.exception.UnauthorizedException;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.CommentRepository;
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

    @Autowired
    private CommentRepository commentRepository;

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
    public void itShouldCreateAPost() throws ForbiddenException {
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
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotCreateAPostInvalidToken() {
        String content = "Test content";
        assertThatThrownBy(() -> this.postService.create("Bearer " + token + "invalidToken", content))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotCreateAPostWithBearerAndWithoutToken() {
        String content = "Test content";
        assertThatThrownBy(() -> this.postService.create("Bearer ", content))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldComment() throws ForbiddenException, NotFoundException {
        Post post = this.postService.create("Bearer " + token, "Test content");
        Comment comment = new Comment();
        User author = this.userService.validateToken("Bearer " + token);
        comment.setAuthor(author);
        comment.setMessage("Test comment");
        comment = this.commentRepository.save(comment);
        post = this.postService.comment(post.getId(), comment);
        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(post.getComments().get(0).getAuthor().getUsername()).isEqualTo(USERNAME);
        assertThat(post.getComments().get(0).getMessage()).isEqualTo("Test comment");
        assertThat(post.getComments().get(0).getAuthor().getPassword()).isNull();
    }

    @Test
    public void itShouldLikePost() throws ForbiddenException, NotFoundException {
        Post post = this.postService.create("Bearer " + token, "Test content");
        assertThat(post.getLikes().size()).isEqualTo(0);
        Post returnedPost = this.postService.like("Bearer " + token, post.getId());
        post = this.postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
        assertThat(post.getLikes().size()).isEqualTo(1);
        assertThat(post.getLikes().get(0).getId()).isEqualTo(user.getId());
        assertThat(returnedPost.getId()).isEqualTo(post.getId());
        assertThat(returnedPost.getLikes().get(0).getPassword()).isNull();
    }

    @Test
    public void itShouldNotLikePostWithoutBearer() {
        assertThatThrownBy(() -> this.postService.like(token, "whatever"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotLikePostWithInvalidToken() {
        assertThatThrownBy(() -> this.postService.like("Bearer " + token + "invalidToken", "whatever"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void irShouldNotLikePostWhenNotExists() {
        assertThatThrownBy(() -> this.postService.like("Bearer " + token, "whatever"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    public void itShouldRemoveLikePost() throws ForbiddenException, NotFoundException {
        Post post = this.postService.create("Bearer " + token, "Test content");
        assertThat(post.getLikes().size()).isEqualTo(0);
        this.postService.like("Bearer " + token, post.getId());
        post = this.postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
        assertThat(post.getLikes().size()).isEqualTo(1);
        assertThat(post.getLikes().get(0).getId()).isEqualTo(user.getId());
        this.postService.like("Bearer " + token, post.getId());
        post = this.postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
        assertThat(post.getLikes().size()).isEqualTo(0);
    }

    @Test
    public void itShouldGetPostById() throws NotFoundException, ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        Post result = this.postService.get(post.getId());
        assertThat(result.getId()).isEqualTo(post.getId());
        assertThat(result.getMessage()).isEqualTo(post.getMessage());
        assertThat(result.getAuthor().getId()).isEqualTo(user.getId());
    }

    @Test
    public void itShouldNotGetPostById() throws NotFoundException, ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.get(post.getId() + "invalidId"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    public void itShouldGetByAuthorName() throws NotFoundException, InvalidCredentialsException, ForbiddenException {
        int size = 10;

        for (int i = 0; i < size; i++) {
            String content = "Test content " + i;
            Post created = this.postService.create("Bearer " + token, content);
            this.postService.like("Bearer " + token, created.getId());
        }
        User anotherUser = new User()
                .setUsername("anotherUser")
                .setEmail("another@email.com")
                .setPassword("anotherPassword");
        this.userRepository.save(anotherUser);
        String anotherToken = this.userService.signin(anotherUser);
        for (int i = 0; i < size; i++) {
            String content = "Another Test content " + i;
            Post created = this.postService.create("Bearer " + anotherToken, content);
            this.postService.like("Bearer " + anotherToken, created.getId());
        }

        List<Post> anotherUserPosts = this.postService.getByAuthorName("anotherUser");
        List<Post> userPosts = this.postService.getByAuthorName(user.getUsername());

        assertThat(anotherUserPosts.size()).isEqualTo(size);
        assertThat(userPosts.size()).isEqualTo(size);

        for (Post post : anotherUserPosts) {
            assertThat(post.getAuthor().getUsername()).isEqualTo("anotherUser");
        }

        for (Post post : userPosts) {
            assertThat(post.getAuthor().getUsername()).isEqualTo(user.getUsername());
        }
    }

    @Test
    public void itShouldDeletePost() throws NotFoundException, ForbiddenException, UnauthorizedException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        Post deletedPost = this.postService.delete("Bearer " + this.token, post.getId());
        assertThatThrownBy(() -> this.postService.get(post.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
        assertThat(this.postRepository.findById(deletedPost.getId())).isEmpty();
        assertThat(deletedPost.getId()).isEqualTo(post.getId());
    }

    @Test
    public void itShouldNotDeletePostTokenWithoutBearer() throws ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete(token, post.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotDeletePostInvalidToken() throws ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + token + "invalidToken", post.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotDeletePostNotFound() throws ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + token, post.getId() + "invalidId"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Post not found");
    }

    @Test
    public void itShouldNotDeletePostNotAuthor() throws InvalidCredentialsException, ForbiddenException {
        String content = "Test content";
        Post post = this.postService.create("Bearer " + token, content);
        User anotherUser = new User()
                .setUsername("anotherUser")
                .setEmail("another@email.com")
                .setPassword("anotherPassword");
        this.userRepository.save(anotherUser);
        String anotherToken = this.userService.signin(anotherUser);
        assertThatThrownBy(() -> this.postService.delete("Bearer " + anotherToken, post.getId()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("You are not the author of this post");
    }
}
