package br.com.rodolfo.social.service;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.model.Comment;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.model.User;
import br.com.rodolfo.social.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Post create(String token, String message) throws IllegalArgumentException, ForbiddenException {
        if (message == null || message.isEmpty())
            throw new IllegalArgumentException("Message cannot be empty");

        User user = userService.validateToken(token).setPassword(null);
        Post post = new Post();
        post.setAuthor(user);
        post.setMessage(message);
        return postRepository.save(post);
    }

    public Post comment(String postId, Comment comment) throws NotFoundException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
        post.getComments().add(comment);
        Post saved = postRepository.save(post);
        return saved.hidePasswords();
    }

    public Post like(String token, String postId) throws IllegalArgumentException, ForbiddenException, NotFoundException {
        User user = userService.validateToken(token).setPassword(null);

        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));

        if (post.getLikes().contains(user))
            post.getLikes().remove(user);
        else
            post.getLikes().add(user);

        postRepository.save(post);
        post.getAuthor().setPassword(null);
        return post;
    }

    public Post get(String id) throws NotFoundException {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
        post.hidePasswords();
        return post;
    }

    public boolean exists(String id) {
        return postRepository.existsById(id);
    }


    public List<Post> getByAuthorName(String authorName) throws NotFoundException {
        User user = userService.getByName(authorName);
        List<Post> posts = postRepository.findByAuthorId(user.getId());
        posts.forEach(Post::hidePasswords);
        return posts;
    }

    public Post delete(String token, String id) throws NotFoundException, ForbiddenException {
        User user = userService.validateToken(token);

        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId()))
            throw new ForbiddenException("You are not the author of this post");

        post.getComments().forEach(comment -> {
            try {
                commentService.delete(token, comment.getId());
            } catch (Exception ignored) {
            }
        });

        this.postRepository.delete(post);
        return post;
    }
}
