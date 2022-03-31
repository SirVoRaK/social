package br.com.rodolfo.social.feed;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.InvalidCredentialsException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.PostRepository;
import br.com.rodolfo.social.repository.UserRepository;
import br.com.rodolfo.social.service.PostService;
import br.com.rodolfo.social.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
public class FeedTest {
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        userService = new UserService(userRepository);
        postService = new PostService(postRepository, userService);
    }

    @AfterEach
    public void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void itShouldReturnPostsOfUsers() throws InvalidCredentialsException, ForbiddenException, NotFoundException {
        User user1 = new User()
                .setUsername("user1")
                .setEmail("user1@email.com")
                .setPassword("123");
        User user2 = new User()
                .setUsername("user2")
                .setEmail("user2@email.com")
                .setPassword("123");
        User user3 = new User()
                .setUsername("user3")
                .setEmail("user3@email.com")
                .setPassword("123");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        String token1 = userService.signin(user1);
        String token2 = userService.signin(user2);
        String token3 = userService.signin(user3);

        postService.create("Bearer " + token1, "Post 1");

        // 14 posts
        postService.create("Bearer " + token2, "Post 2");
        postService.create("Bearer " + token2, "Post 2");
        postService.create("Bearer " + token2, "Post 2");
        postService.create("Bearer " + token2, "Post 2");
        postService.create("Bearer " + token3, "Post 3");
        postService.create("Bearer " + token3, "Post 3");
        postService.create("Bearer " + token3, "Post 3");
        postService.create("Bearer " + token3, "Post 3");

        userService.follow("Bearer " + token1, user2.getUsername());
        userService.follow("Bearer " + token1, user3.getUsername());

        List<String> usernames = List.of(user2.getUsername(), user3.getUsername());

        List<Post> posts = new Feed(usernames, postService).sortedByDate();

        assertThat(posts.size()).isEqualTo(8);

        LocalDateTime lastDate = null;
        for (Post post : posts) {
            if (lastDate != null) assertThat(post.getDate().isAfter(lastDate)).isTrue();
            lastDate = post.getDate();
        }
    }
}
