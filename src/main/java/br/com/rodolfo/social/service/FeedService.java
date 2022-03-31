package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.feed.Feed;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    public FeedService() {
    }

    public FeedService(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public List<Post> getFeed(String token) throws ForbiddenException, NotFoundException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Token must start with Bearer");
        User user;
        try {
            user = userService.validateToken(token).orElseThrow(() -> new ForbiddenException("Invalid token"));
        } catch (Exception e) {
            throw new ForbiddenException("Invalid token");
        }
        List<String> following = user.getFollowing();
        return new Feed(following, this.postService).shuffled();
    }
}
