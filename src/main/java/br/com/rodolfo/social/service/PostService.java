package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public Post create(String token, String message) throws IllegalArgumentException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");

        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        Optional<User> userOpt;
        try {
            userOpt = userService.validateToken(token);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        if (userOpt.isEmpty())
            throw new IllegalArgumentException("Invalid token");

        User user = userOpt.get();
        try {
            user.setPassword(null);
        } catch (NoSuchAlgorithmException ignored) {
        }
        Post post = new Post();
        post.setAuthor(user);
        post.setMessage(message);
        return postRepository.save(post);
    }

    public Post comment(String postId, Comment comment) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) throw new IllegalArgumentException("Post not found");
        Post post = postOpt.get();
        post.getComments().add(comment);
        Post saved = postRepository.save(post);
        saved.hidePasswords();
        return saved;
    }

    public Post like(String token, String postId) throws IllegalArgumentException {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");

        User user;
        try {
            user = userService.validateToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        try {
            user.setPassword(null);
        } catch (NoSuchAlgorithmException ignored) {
        }

        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty())
            throw new IllegalArgumentException("Post not found");

        Post post = postOpt.get();
        if (post.getLikes().contains(user))
            post.getLikes().remove(user);
        else
            post.getLikes().add(user);
        postRepository.save(post);
        try {
            post.getAuthor().setPassword(null);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return post;
    }

    public Post get(String id) throws NotFoundException {
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) throw new NotFoundException("Post not found");
        Post post = postOpt.get();
        post.hidePasswords();
        return post;
    }


    public List<Post> getByAuthorName(String authorName, Integer start, Integer end) throws NotFoundException {
        User user = userService.getByName(authorName);
        List<Post> posts = postRepository.findByAuthorId(user.getId(), PageRequest.of(start, end));
        posts.forEach(Post::hidePasswords);
        return posts;
    }

    public Post delete(String token, String id) {
        if (!token.startsWith("Bearer "))
            throw new IllegalArgumentException("Authorization header must start with 'Bearer '");

        User user;
        try {
            user = userService.validateToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token");
        }

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty())
            throw new IllegalArgumentException("Post not found");

        Post post = postOpt.get();
        if (!post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("You are not the author of this post");

        postRepository.delete(post);
        return post;
    }
}
