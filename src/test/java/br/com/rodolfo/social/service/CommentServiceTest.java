package br.com.rodolfo.social.service;

import br.com.rodolfo.social.SocialApplicationTests;
import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataMongoTest
public class CommentServiceTest {
    public static final String USERNAME = "TestUser";
    public static final String EMAIL = "test@email.com";
    public static final String PASSWORD = "Password@123456";

    @Autowired
    private CommentRepository commentRepository;
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;
    private UserService userService;

    @Autowired
    private PostRepository postRepository;
    private PostService postService;

    private User user;
    private String token;

    @BeforeEach
    public void setUp() throws InvalidCredentialsException {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        postRepository.deleteAll();

        userService = new UserService(userRepository);
        commentService = new CommentService(commentRepository, userService);
        postService = new PostService(postRepository, userService);

        User user = new User()
                .setUsername(USERNAME)
                .setEmail(EMAIL)
                .setPassword(PASSWORD);
        this.user = this.userRepository.save(user);
        this.token = this.userService.signin(user);
    }

    @AfterEach
    public void tearDown() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void itShouldCreateComment() throws ForbiddenException, NotFoundException {
        Comment comment = commentService.create("Bearer " + token, "Test comment");
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getMessage()).isEqualTo("Test comment");
        assertThat(comment.getAuthor().getPassword()).isNull();
        assertThat(comment.getAuthor().getUsername()).isEqualTo(USERNAME);

        comment = this.commentRepository.findById(comment.getId()).orElseThrow(() -> new RuntimeException("Comment not found"));

        assertThat(comment.getAuthor().getUsername()).isEqualTo(USERNAME);
        assertThat(comment.getAuthor().getId()).isEqualTo(user.getId());
        assertThat(comment.getMessage()).isEqualTo("Test comment");
    }

    @Test
    public void itShouldNotCreateCommentWithoutBearer() {
        assertThatThrownBy(() -> this.commentService.create(token, "Test comment"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotCreateCommentWithInvalidToken() {
        assertThatThrownBy(() -> this.commentService.create("Bearer " + token + "invalidToken", "Test comment"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotCreateWithEmptyMessage() {
        assertThatThrownBy(() -> this.commentService.create("Bearer " + token, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldNotCreateWithNullMessage() {
        assertThatThrownBy(() -> this.commentService.create("Bearer " + token, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldReply() throws ForbiddenException, NotFoundException {
        Comment comment = this.commentService.create("Bearer " + token, "Test comment");
        Comment reply = this.commentService.reply("Bearer " + token, comment.getId(), "Reply comment");

        assertThat(reply.getId()).isNotNull();
        assertThat(reply.getMessage()).isEqualTo("Reply comment");
        assertThat(reply.getAuthor().getPassword()).isNull();
        assertThat(reply.getAuthor().getUsername()).isEqualTo(USERNAME);

        Comment commentFromRepository = this.commentRepository.findById(comment.getId()).orElseThrow(() -> new RuntimeException("Comment not found"));
        assertThat(commentFromRepository.getComments().size()).isEqualTo(1);
        assertThat(commentFromRepository.getComments().get(0).getId()).isEqualTo(reply.getId());
    }

    @Test
    public void itShouldNotReplyWithoutBearer() {
        assertThatThrownBy(() -> this.commentService.reply(token, "whatever", "Test comment"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotReplyWithInvalidToken() {
        assertThatThrownBy(() -> this.commentService.reply("Bearer " + token + "invalidToken", "whatever", "Test comment"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotReplyWithEmptyMessage() {
        assertThatThrownBy(() -> this.commentService.reply("Bearer " + token, "whatever", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldNotReplyWithNullMessage() {
        assertThatThrownBy(() -> this.commentService.reply("Bearer " + token, "whatever", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Message cannot be empty");
    }

    @Test
    public void itShouldDeleteOneComment() throws ForbiddenException, NotFoundException {
        Comment comment = this.commentService.create("Bearer " + token, "Test comment");
        this.commentService.delete("Bearer " + token, comment.getId(), postService);
        assertThat(this.commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    public void itShouldDeleteCommentFromPost() throws ForbiddenException, NotFoundException {
        Post post = this.postService.create("Bearer " + token, "Test post");
        Comment comment = this.commentService.create("Bearer " + token, "Test comment", post.getId(), this.postService);
        this.commentService.delete("Bearer " + token, comment.getId(), postService);
        post = this.postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
        assertThat(this.commentRepository.findById(comment.getId())).isEmpty();
        assertThat(this.postRepository.findById(post.getId()).orElseThrow(() -> new RuntimeException("Post not found")).getComments().size()).isEqualTo(0);
    }

    @Test
    public void itShouldDeleteCommentFromComment() throws ForbiddenException, NotFoundException {
        Comment comment1 = this.commentService.create("Bearer " + token, "Test comment");
        Comment comment2 = this.commentService.reply("Bearer " + token, comment1.getId(), "Test comment");

        this.commentService.delete("Bearer " + token, comment2.getId(), postService);

        comment1 = this.commentRepository.findById(comment1.getId()).orElseThrow(() -> new RuntimeException("Comment not found"));
        assertThat(this.commentRepository.findById(comment2.getId())).isEmpty();
        assertThat(comment1.getComments().size()).isEqualTo(0);
    }

    @Test
    public void itShouldNotDeleteWithoutBearer() {
        assertThatThrownBy(() -> this.commentService.delete(token, "whatever", postService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SocialApplicationTests.WITHOUT_BEARER_MESSAGE);
    }

    @Test
    public void itShouldNotDeleteWithInvalidToken() {
        assertThatThrownBy(() -> this.commentService.delete("Bearer " + token + "invalidToken", "whatever", postService))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Invalid token");
    }

    @Test
    public void itShouldNotDeleteWhenNotFound() {
        assertThatThrownBy(() -> this.commentService.delete("Bearer " + token, "whatever", postService))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    public void itShouldNotDeleteWhenNotAuthor() throws ForbiddenException, NotFoundException, InvalidCredentialsException {
        Comment comment = this.commentService.create("Bearer " + token, "Test comment");

        User anotherUser = new User()
                .setPassword("Password@123")
                .setUsername("anotherUser")
                .setEmail("another@email.com");
        anotherUser = this.userService.create(anotherUser, false);
        String anotherToken = this.userService.signin(anotherUser);

        assertThatThrownBy(() -> this.commentService.delete("Bearer " + anotherToken, comment.getId(), postService))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("You are not the author of this comment");
    }

    @Test
    public void itShouldDeleteAllCommentsOfComments() throws ForbiddenException, NotFoundException {
        Comment comment = this.commentService.create("Bearer " + token, "Test comment");

        Comment reply = this.commentService.reply("Bearer " + token, comment.getId(), "Reply comment");
        Comment reply2 = this.commentService.reply("Bearer " + token, comment.getId(), "Reply2 comment");

        Comment replyReply = this.commentService.reply("Bearer " + token, reply.getId(), "Reply reply comment");
        Comment reply2Reply = this.commentService.reply("Bearer " + token, reply2.getId(), "Reply2 reply comment");

        assertThat(this.commentRepository.findAll().size()).isEqualTo(5);

        this.commentService.delete("Bearer " + token, comment.getId(), postService);

        assertThat(this.commentRepository.findAll().size()).isEqualTo(0);
    }
}
