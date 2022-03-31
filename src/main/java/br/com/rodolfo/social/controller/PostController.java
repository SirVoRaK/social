package br.com.rodolfo.social.controller;

import br.com.rodolfo.social.exception.ForbiddenException;
import br.com.rodolfo.social.exception.NotFoundException;
import br.com.rodolfo.social.forms.PostForm;
import br.com.rodolfo.social.model.Post;
import br.com.rodolfo.social.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/")
    public ResponseEntity<Post> createPost(@RequestBody PostForm postForm, @RequestHeader("Authorization") String token) throws ForbiddenException {
        Post post = postService.create(token, postForm.getMessage());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable("id") String id, @RequestHeader("Authorization") String token) throws NotFoundException, ForbiddenException {
        Post post = postService.delete(token, id);
        return ResponseEntity.ok(post.hidePasswords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) throws NotFoundException {
        Post post = postService.get(id);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<Post> like(@PathVariable("id") String id, @RequestHeader("Authorization") String token) throws ForbiddenException, NotFoundException {
        Post post = postService.like(token, id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Iterable<Post>> getPostsByUser(@PathVariable("username") String username) throws NotFoundException {
        List<Post> posts = postService.getByAuthorName(username);
        return ResponseEntity.ok(posts);
    }
}
